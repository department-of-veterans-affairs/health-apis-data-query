package gov.va.api.health.dataquery.service.controller.procedure;

import static gov.va.api.health.dataquery.service.controller.Transformers.firstPayloadItem;
import static gov.va.api.health.dataquery.service.controller.Transformers.hasPayload;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.argonaut.api.resources.Procedure;
import gov.va.api.health.argonaut.api.resources.Procedure.Bundle;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.dataquery.service.controller.AbstractIncludesIcnMajig;
import gov.va.api.health.dataquery.service.controller.Bundler;
import gov.va.api.health.dataquery.service.controller.Bundler.BundleContext;
import gov.va.api.health.dataquery.service.controller.CountParameter;
import gov.va.api.health.dataquery.service.controller.DateTimeParameter;
import gov.va.api.health.dataquery.service.controller.PageLinks;
import gov.va.api.health.dataquery.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.dataquery.service.controller.Parameters;
import gov.va.api.health.dataquery.service.controller.ResourceExceptions.NotFound;
import gov.va.api.health.dataquery.service.controller.Validator;
import gov.va.api.health.dataquery.service.controller.WitnessProtection;
import gov.va.api.health.dataquery.service.controller.procedure.ProcedureRepository.PatientAndDateSpecification;
import gov.va.api.health.dataquery.service.mranderson.client.MrAndersonClient;
import gov.va.api.health.dataquery.service.mranderson.client.Query;
import gov.va.api.health.dstu2.api.resources.OperationOutcome;
import gov.va.dvp.cdw.xsd.model.CdwProcedure101Root;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Request Mappings for Procedure Profile, see
 * https://www.fhir.org/guides/argonaut/r2/StructureDefinition-argo-procedure.html for
 * implementation details.
 */
@Slf4j
@Builder
@Validated
@RestController
@RequestMapping(
  value = {"/dstu2/Procedure"},
  produces = {"application/json", "application/json+fhir", "application/fhir+json"}
)
public class ProcedureController {
  private final Datamart datamart = new Datamart();

  private final boolean defaultToDatamart;

  /**
   * Optional ID for a patient with procedure data that can secretly service requests for {@link
   * #withoutRecordsId}.
   */
  private final String withRecordsId;

  /**
   * Optional ID for a patient with no procedure data, whose requests can be secretly serviced by
   * {@link #withRecordsId}.
   */
  private final String withoutRecordsId;

  private final String withRecordsDisplay;

  private final String withoutRecordsDisplay;

  private Transformer transformer;

  private MrAndersonClient mrAndersonClient;

  private Bundler bundler;

  private ProcedureRepository repository;

  private WitnessProtection witnessProtection;

  /** Let's try something new. */
  @Autowired
  public ProcedureController(
      @Value("${datamart.procedure}") boolean defaultToDatamart,
      @Value("${procedure.test-patient-workaround.id-with-records:}") String withRecordsId,
      @Value("${procedure.test-patient-workaround.id-without-records:}") String withoutRecordsId,
      @Value("${procedure.test-patient-workaround.display-with-records:}")
          String withRecordsDisplay,
      @Value("${procedure.test-patient-workaround.display-without-records:}")
          String withoutRecordsDisplay,
      Transformer transformer,
      MrAndersonClient mrAndersonClient,
      Bundler bundler,
      ProcedureRepository repository,
      WitnessProtection witnessProtection) {
    this.defaultToDatamart = defaultToDatamart;
    this.withRecordsId = withRecordsId;
    this.withoutRecordsId = withoutRecordsId;
    this.withRecordsDisplay = withRecordsDisplay;
    this.withoutRecordsDisplay = withoutRecordsDisplay;
    this.transformer = transformer;
    this.mrAndersonClient = mrAndersonClient;
    this.bundler = bundler;
    this.repository = repository;
    this.witnessProtection = witnessProtection;
  }

  private Procedure.Bundle bundle(MultiValueMap<String, String> parameters, int page, int count) {
    CdwProcedure101Root root = search(parameters);
    LinkConfig linkConfig =
        LinkConfig.builder()
            .path("Procedure")
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(root.getRecordCount())
            .build();
    return bundler.bundle(
        BundleContext.of(
            linkConfig,
            root.getProcedures() == null
                ? Collections.emptyList()
                : root.getProcedures().getProcedure(),
            transformer,
            Procedure.Entry::new,
            Procedure.Bundle::new));
  }

  /**
   * Disguise procedure data for patient-with-records as data for patient-without-records. {@link
   * #withRecordsId} is replaced with {@link #withoutRecordsId} and {@link #withRecordsDisplay} is
   * replaced with {@link #withoutRecordsDisplay}.
   */
  @SneakyThrows
  private <T> T disguiseAsPatientWithoutRecords(T withRecords, Class<T> clazz) {
    log.info(
        "Disguising procedure for patient {} ({}) as patient {} ({}).",
        withRecordsId,
        withRecordsDisplay,
        withoutRecordsId,
        withoutRecordsDisplay);
    ObjectMapper mapper = JacksonConfig.createMapper();
    String withRecordsString = mapper.writeValueAsString(withRecords);
    String withoutRecordsString =
        withRecordsString
            .replaceAll(withRecordsId, withoutRecordsId)
            .replaceAll(withRecordsDisplay, withoutRecordsDisplay);
    return mapper.readValue(withoutRecordsString, clazz);
  }

  /**
   * In some environments, it is necessary to use procedure data for one patient, {@link
   * #withRecordsId}, to service requests for another patient, {@link #withoutRecordsId}, that has
   * none of its own. The displayed name of the patient-with-records, {@link #withRecordsDisplay},
   * is also replaced by the displayed name of the patient-without-records, {@link
   * #withoutRecordsDisplay}. This method returns {@code true} if patient {@link #withoutRecordsId}
   * is requested when all four of these values are configured.
   */
  private boolean isPatientWithoutRecords(String patient) {
    return patient.equals(withoutRecordsId)
        && isNotBlank(withRecordsId)
        && isNotBlank(withoutRecordsId)
        && isNotBlank(withRecordsDisplay)
        && isNotBlank(withoutRecordsDisplay);
  }

  /** Read by id. */
  @GetMapping(value = {"/{publicId}"})
  public Procedure read(
      @RequestHeader(value = "Datamart", defaultValue = "") String datamartHeader,
      @PathVariable("publicId") String publicId,
      @RequestHeader(value = "X-VA-ICN", required = false) String icnHeader) {
    if (datamart.isDatamartRequest(datamartHeader)) {
      return datamart.read(publicId, icnHeader);
    }
    if (isNotBlank(icnHeader) && isPatientWithoutRecords(icnHeader)) {
      return disguiseAsPatientWithoutRecords(
          read("false", publicId, withRecordsId), Procedure.class);
    }
    return transformer.apply(
        firstPayloadItem(
            hasPayload(search(Parameters.forIdentity(publicId)).getProcedures()).getProcedure()));
  }

  /** Read by id. */
  @GetMapping(
    value = {"/{publicId}"},
    headers = {"raw=true"}
  )
  public String readRaw(
      @PathVariable("publicId") String publicId,
      @RequestHeader(value = "X-VA-ICN", required = false) String icnHeader,
      HttpServletResponse response) {
    ProcedureEntity entity = datamart.readRaw(publicId);
    if (isNotBlank(icnHeader)
        && isPatientWithoutRecords(icnHeader)
        && entity.icn().equals(withRecordsId)) {
      log.info(
          "Procedure Hack: Setting includes header to magic patient: {}",
          icnHeader.replaceAll("[\r\n]", ""));
      AbstractIncludesIcnMajig.addHeader(
          response, AbstractIncludesIcnMajig.encodeHeaderValue(icnHeader));
    } else {
      AbstractIncludesIcnMajig.addHeader(
          response, AbstractIncludesIcnMajig.encodeHeaderValue(entity.icn()));
    }
    return entity.payload();
  }

  private CdwProcedure101Root search(MultiValueMap<String, String> params) {
    Query<CdwProcedure101Root> query =
        Query.forType(CdwProcedure101Root.class)
            .profile(Query.Profile.ARGONAUT)
            .resource("Procedure")
            .version("1.01")
            .parameters(params)
            .build();
    return hasPayload(mrAndersonClient.search(query));
  }

  /** Search by _id. */
  @GetMapping(params = {"_id"})
  public Procedure.Bundle searchById(
      @RequestHeader(value = "Datamart", defaultValue = "") String datamartHeader,
      @RequestHeader(value = "X-VA-ICN", required = false) String icnHeader,
      @RequestParam("_id") String publicId,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    if (datamart.isDatamartRequest(datamartHeader)) {
      return datamart.searchById(publicId, icnHeader, page, count);
    }
    return bundle(
        Parameters.builder()
            .add("identifier", publicId)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  /** Search by Identifier. */
  @GetMapping(params = {"identifier"})
  public Procedure.Bundle searchByIdentifier(
      @RequestHeader(value = "Datamart", defaultValue = "") String datamartHeader,
      @RequestHeader(value = "X-VA-ICN", required = false) String icnHeader,
      @RequestParam("identifier") String id,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    return searchById(datamartHeader, icnHeader, id, page, count);
  }

  /** Search by patient and date if provided. */
  @GetMapping(params = {"patient"})
  public Procedure.Bundle searchByPatientAndDate(
      @RequestHeader(value = "Datamart", defaultValue = "") String datamartHeader,
      @RequestParam("patient") String patient,
      @RequestParam(value = "date", required = false) @Valid @DateTimeParameter @Size(max = 2)
          String[] date,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    if (datamart.isDatamartRequest(datamartHeader)) {
      return datamart.searchByPatient(patient, date, page, count);
    }
    if (isPatientWithoutRecords(patient)) {
      return disguiseAsPatientWithoutRecords(
          searchByPatientAndDate("false", withRecordsId, date, page, count),
          Procedure.Bundle.class);
    }
    return bundle(
        Parameters.builder()
            .add("patient", patient)
            .addAll("date", date)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  /** Hey, this is a validate endpoint. It validates. */
  @PostMapping(
    value = "/$validate",
    consumes = {"application/json", "application/json+fhir", "application/fhir+json"}
  )
  public OperationOutcome validate(@RequestBody Procedure.Bundle bundle) {
    return Validator.create().validate(bundle);
  }

  public interface Transformer
      extends Function<CdwProcedure101Root.CdwProcedures.CdwProcedure, Procedure> {}

  /**
   * This class is being used to help organize the code such that all the datamart logic is
   * contained together. In the future when Mr. Anderson support is dropped, this class can be
   * eliminated.
   */
  private class Datamart {

    private Bundle bundle(
        MultiValueMap<String, String> parameters, List<Procedure> reports, int totalRecords) {
      PageLinks.LinkConfig linkConfig =
          PageLinks.LinkConfig.builder()
              .path("Procedure")
              .queryParams(parameters)
              .page(Parameters.pageOf(parameters))
              .recordsPerPage(Parameters.countOf(parameters))
              .totalRecords(totalRecords)
              .build();
      return bundler.bundle(
          Bundler.BundleContext.of(
              linkConfig,
              reports,
              Function.identity(),
              Procedure.Entry::new,
              Procedure.Bundle::new));
    }

    private Bundle bundle(
        MultiValueMap<String, String> parameters, int count, Page<ProcedureEntity> entities) {
      log.info("Search {} found {} results", parameters, entities.getTotalElements());
      if (count == 0) {
        return bundle(parameters, emptyList(), (int) entities.getTotalElements());
      }

      return bundle(
          parameters,
          replaceReferences(
                  entities
                      .get()
                      .map(ProcedureEntity::asDatamartProcedure)
                      .collect(Collectors.toList()))
              .stream()
              .map(this::transform)
              .collect(Collectors.toList()),
          (int) entities.getTotalElements());
    }

    ProcedureEntity findById(String publicId) {
      Optional<ProcedureEntity> entity = repository.findById(witnessProtection.toCdwId(publicId));
      return entity.orElseThrow(() -> new NotFound(publicId));
    }

    boolean isDatamartRequest(String datamartHeader) {
      if (StringUtils.isBlank(datamartHeader)) {
        return defaultToDatamart;
      }
      return BooleanUtils.isTrue(BooleanUtils.toBooleanObject(datamartHeader));
    }

    private PageRequest page(int page, int count) {
      return PageRequest.of(page - 1, count == 0 ? 1 : count, ProcedureEntity.naturalOrder());
    }

    Procedure read(String publicId, String icnHeader) {
      DatamartProcedure procedure = findById(publicId).asDatamartProcedure();
      replaceReferences(List.of(procedure));
      Procedure fhir = transform(procedure);
      if (isNotBlank(icnHeader) && isPatientWithoutRecords(icnHeader)) {
        fhir = disguiseAsPatientWithoutRecords(fhir, Procedure.class);
      }
      return fhir;
    }

    ProcedureEntity readRaw(String publicId) {
      return findById(publicId);
    }

    Collection<DatamartProcedure> replaceReferences(Collection<DatamartProcedure> resources) {
      witnessProtection.registerAndUpdateReferences(
          resources,
          resource ->
              Stream.of(
                  resource.patient(),
                  resource.encounter().orElse(null),
                  resource.location().orElse(null)));
      return resources;
    }

    Bundle searchById(String publicId, String icnHeader, int page, int count) {
      Procedure resource = read(publicId, icnHeader);
      return bundle(
          Parameters.builder()
              .add("identifier", publicId)
              .add("page", page)
              .add("_count", count)
              .build(),
          resource == null || count == 0 ? emptyList() : List.of(resource),
          resource == null ? 0 : 1);
    }

    Bundle searchByPatient(String patient, String[] date, int page, int count) {
      final boolean isPatientWithoutRecords = isPatientWithoutRecords(patient);
      if (isPatientWithoutRecords) {
        patient = withRecordsId;
      }
      String icn = witnessProtection.toCdwId(patient);
      PatientAndDateSpecification spec =
          PatientAndDateSpecification.builder().patient(icn).dates(date).build();
      log.info("Looking for {} ({}) {}", patient, icn, spec);
      Page<ProcedureEntity> pageOfProcedures = repository.findAll(spec, page(page, count));
      Bundle bundle =
          bundle(
              Parameters.builder()
                  .add("patient", patient)
                  .addAll("date", date)
                  .add("page", page)
                  .add("_count", count)
                  .build(),
              count,
              pageOfProcedures);
      if (isPatientWithoutRecords) {
        bundle = disguiseAsPatientWithoutRecords(bundle, Bundle.class);
      }
      return bundle;
    }

    Procedure transform(DatamartProcedure dm) {
      return DatamartProcedureTransformer.builder().datamart(dm).build().toFhir();
    }
  }
}
