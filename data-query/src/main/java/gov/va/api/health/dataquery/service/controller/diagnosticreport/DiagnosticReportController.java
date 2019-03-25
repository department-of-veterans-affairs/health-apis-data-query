package gov.va.api.health.dataquery.service.controller.diagnosticreport;

import static gov.va.api.health.dataquery.service.controller.Transformers.firstPayloadItem;
import static gov.va.api.health.dataquery.service.controller.Transformers.hasPayload;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.dataquery.api.resources.DiagnosticReport;
import gov.va.api.health.dataquery.api.resources.OperationOutcome;
import gov.va.api.health.dataquery.service.controller.Bundler;
import gov.va.api.health.dataquery.service.controller.Bundler.BundleContext;
import gov.va.api.health.dataquery.service.controller.DateTimeParameter;
import gov.va.api.health.dataquery.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.dataquery.service.controller.Parameters;
import gov.va.api.health.dataquery.service.controller.Validator;
import gov.va.api.health.dataquery.service.controller.WitnessProtection;
import gov.va.api.health.dataquery.service.mranderson.client.MrAndersonClient;
import gov.va.api.health.dataquery.service.mranderson.client.Query;
import gov.va.dvp.cdw.xsd.model.CdwDiagnosticReport102Root;
import gov.va.dvp.cdw.xsd.model.CdwDiagnosticReport102Root.CdwDiagnosticReports.CdwDiagnosticReport;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.xml.bind.JAXBContext;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Request Mappings for Diagnostic Report Profile, see
 * https://www.fhir.org/guides/argonaut/r2/StructureDefinition-argo-diagnosticreport.html for
 * implementation details.
 */
@Slf4j
@SuppressWarnings("WeakerAccess")
@Validated
@RestController
@RequestMapping(
  value = {"/api/DiagnosticReport"},
  produces = {"application/json", "application/json+fhir", "application/fhir+json"}
)
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class DiagnosticReportController {
  private MrAndersonClient mrAndersonClient;

  private WitnessProtection witnessProtection;

  private EntityManager entityManager;

  private Transformer transformer;

  private Bundler bundler;

  @SneakyThrows
  private static CdwDiagnosticReport102Root cdwRootObject(String xml) {
    // PETERTODO not diagnostic-report specific
    try (Reader reader = new StringReader(xml)) {
      return (CdwDiagnosticReport102Root)
          JAXBContext.newInstance(CdwDiagnosticReport102Root.class)
              .createUnmarshaller()
              .unmarshal(reader);
    }
  }

  private static String entitiesXml(List<DiagnosticReportEntity> entities) {
    String taggedReports =
        entities
            .stream()
            .map(entity -> "<diagnosticReport>" + entity.document() + "</diagnosticReport>")
            .collect(Collectors.joining());
    String allReports = "<diagnosticReports>" + taggedReports + "</diagnosticReports>";
    return "<root>" + allReports + "</root>";
  }

  // PETERTODO not diagnostic-report specific
  private static void queryAddParameters(
      TypedQuery<?> query, MultiValueMap<String, String> parameters) {
    // PETERTODO examine all params and just skip page and _count
    if (parameters.containsKey("identifier")) {
      query.setParameter("identifier", parameters.getFirst("identifier"));
    }
    if (parameters.containsKey("patient")) {
      query.setParameter("patient", Long.valueOf(parameters.getFirst("patient")));
    }
  }

  // PETERTODO not diagnostic-report specific
  private static String queryTotal(MultiValueMap<String, String> parameters) {
    // PETERTODO refactor to top level
    if (parameters.containsKey("identifier")) {
      return "Select count(dr.id) from DiagnosticReportEntity dr where dr.identifier is :identifier";
    } else if (parameters.containsKey("patient")) {
      return "Select count(dr.id) from DiagnosticReportEntity dr where dr.patientId is :patient";
    } else {
      throw new IllegalArgumentException("Cannot determine total-query");
    }
  }

  @SneakyThrows
  private DiagnosticReport.Bundle searchOldAndNew(
      MultiValueMap<String, String> parameters, int page, int count) {
    DiagnosticReport.Bundle jpaBundle = jpaBundle(parameters, page, count);
    DiagnosticReport.Bundle mrAndersonBundle = mrAndersonBundle(parameters, page, count);
    if (!jpaBundle.equals(mrAndersonBundle)) {
      log.warn(
          "jpa-bundle and mr-anderson bundle do not match. JPA found {} results and mr-anderson found {} results.",
          jpaBundle.total(),
          mrAndersonBundle.total());
      log.warn("jpa-bundle is {}", JacksonConfig.createMapper().writeValueAsString(jpaBundle));
      log.warn(
          "mr-anderson bundle is {}",
          JacksonConfig.createMapper().writeValueAsString(mrAndersonBundle));
    }
    return mrAndersonBundle;
  }

  private DiagnosticReport.Bundle mrAndersonBundle(
      MultiValueMap<String, String> parameters, int page, int count) {
    CdwDiagnosticReport102Root cdwRoot = mrAndersonSearch(parameters);
    final List<CdwDiagnosticReport> reports =
        cdwRoot.getDiagnosticReports() == null
            ? Collections.emptyList()
            : cdwRoot.getDiagnosticReports().getDiagnosticReport();
    return bundle(parameters, page, count, cdwRoot.getRecordCount().intValue(), reports);
  }

  private DiagnosticReport.Bundle bundle(
      MultiValueMap<String, String> parameters,
      int page,
      int count,
      int totalRecords,
      List<CdwDiagnosticReport> xmlReports) {
    LinkConfig linkConfig =
        LinkConfig.builder()
            .path("DiagnosticReport")
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(totalRecords)
            .build();
    return bundler.bundle(
        BundleContext.of(
            linkConfig,
            xmlReports == null ? Collections.emptyList() : xmlReports,
            transformer,
            DiagnosticReport.Entry::new,
            DiagnosticReport.Bundle::new));
  }

  @SneakyThrows
  private DiagnosticReport.Bundle jpaBundle(
      MultiValueMap<String, String> publicParameters, int page, int count) {
    MultiValueMap<String, String> cdwParameters =
        witnessProtection.replacePublicIdsWithCdwIds(publicParameters);
    CdwDiagnosticReport102Root cdwRoot = cdwRoot(publicParameters, cdwParameters, page, count);
    return bundle(
        publicParameters,
        page,
        count,
        totalRecordsCount(cdwParameters),
        cdwRoot.getDiagnosticReports().getDiagnosticReport());
  }

  private CdwDiagnosticReport102Root cdwRoot(
      MultiValueMap<String, String> publicParameters,
      MultiValueMap<String, String> cdwParameters,
      int page,
      int count) {
    List<DiagnosticReportEntity> entities = queryForEntities(cdwParameters, page, count);
    String cdwXml = entitiesXml(entities);
    String publicXml =
        witnessProtection.replaceCdwIdsWithPublicIds("DiagnosticReport", publicParameters, cdwXml);
    return cdwRootObject(publicXml);
  }

  // PETERTODO not diagnostic-report specific
  private String query(MultiValueMap<String, String> cdwParameters) {
    // PETERTODO refactor to top level
    if (cdwParameters.containsKey("identifier")) {
      return "Select dr from DiagnosticReportEntity dr where dr.identifier is :identifier";
    } else if (cdwParameters.containsKey("patient")) {
      return "Select dr from DiagnosticReportEntity dr where dr.patientId is :patient";
    } else {
      throw new IllegalArgumentException("Cannot determine query");
    }
  }

  // PETERTODO not diagnostic-report specific
  private List<DiagnosticReportEntity> queryForEntities(
      MultiValueMap<String, String> cdwParameters, int page, int count) {
    TypedQuery<DiagnosticReportEntity> jpqlQuery =
        entityManager.createQuery(query(cdwParameters), DiagnosticReportEntity.class);
    jpqlQuery.setFirstResult((page - 1) * count);
    jpqlQuery.setMaxResults(count);
    queryAddParameters(jpqlQuery, cdwParameters);
    List<DiagnosticReportEntity> results = jpqlQuery.getResultList();
    log.error("Found {} diagnostic reports for parameters {}", results.size(), cdwParameters);
    return results;
  }

  /** Read by identifier. */
  @SneakyThrows
  @GetMapping(value = {"/{publicId}"})
  public DiagnosticReport read(@PathVariable("publicId") String publicId) {
    MultiValueMap<String, String> publicParameters = Parameters.forIdentity(publicId);
    CdwDiagnosticReport102Root mrAndersonCdw = mrAndersonSearch(publicParameters);

    MultiValueMap<String, String> cdwParameters =
        witnessProtection.replacePublicIdsWithCdwIds(publicParameters);
    CdwDiagnosticReport102Root jpaCdw = cdwRoot(publicParameters, cdwParameters, 1, 15);

    DiagnosticReport mrAndersonReport =
        transformer.apply(
            firstPayloadItem(
                hasPayload(mrAndersonCdw.getDiagnosticReports()).getDiagnosticReport()));
    DiagnosticReport jpaReport =
        transformer.apply(
            firstPayloadItem(hasPayload(jpaCdw.getDiagnosticReports()).getDiagnosticReport()));

    if (!jpaReport.equals(mrAndersonReport)) {
      log.warn("jpa read and mr-anderson read do not match.");
      log.warn("jpa report is {}", JacksonConfig.createMapper().writeValueAsString(jpaReport));
      log.warn(
          "mr-anderson report is {}",
          JacksonConfig.createMapper().writeValueAsString(mrAndersonReport));
    }

    return mrAndersonReport;
  }

  private CdwDiagnosticReport102Root mrAndersonSearch(MultiValueMap<String, String> params) {
    Query<CdwDiagnosticReport102Root> query =
        Query.forType(CdwDiagnosticReport102Root.class)
            .profile(Query.Profile.ARGONAUT)
            .resource("DiagnosticReport")
            .version("1.02")
            .parameters(params)
            .build();
    return hasPayload(mrAndersonClient.search(query));
  }

  /** Search by _id. */
  @GetMapping(params = {"_id"})
  public DiagnosticReport.Bundle searchById(
      @RequestParam("_id") String id,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    return searchByIdentifier(id, page, count);
  }

  /** Search by identifier. */
  @GetMapping(params = {"identifier"})
  public DiagnosticReport.Bundle searchByIdentifier(
      @RequestParam("identifier") String identifier,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    MultiValueMap<String, String> parameters =
        Parameters.builder()
            .add("identifier", identifier)
            .add("page", page)
            .add("_count", count)
            .build();
    return searchOldAndNew(parameters, page, count);
  }

  /** Search by patient. */
  @GetMapping(params = {"patient"})
  public DiagnosticReport.Bundle searchByPatient(
      @RequestParam("patient") String patient,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    MultiValueMap<String, String> parameters =
        Parameters.builder().add("patient", patient).add("page", page).add("_count", count).build();
    return searchOldAndNew(parameters, page, count);
  }

  /** Search by Patient+Category. */
  @GetMapping(params = {"patient", "category"})
  public DiagnosticReport.Bundle searchByPatientAndCategory(
      @RequestParam("patient") String patient,
      @RequestParam("category") String category,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    // PETERTODO
    return mrAndersonBundle(
        Parameters.builder()
            .add("patient", patient)
            .add("category", category)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  /** Search by Patient+Category+Date. */
  @GetMapping(params = {"patient", "category", "date"})
  public DiagnosticReport.Bundle searchByPatientAndCategoryAndDate(
      @RequestParam("patient") String patient,
      @RequestParam("category") String category,
      @RequestParam(value = "date", required = false) @Valid @DateTimeParameter @Size(max = 2)
          String[] date,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    // PETERTODO
    return mrAndersonBundle(
        Parameters.builder()
            .add("patient", patient)
            .add("category", category)
            .addAll("date", date)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  /** Search by Patient+Code. */
  @GetMapping(params = {"patient", "code"})
  public DiagnosticReport.Bundle searchByPatientAndCode(
      @RequestParam("patient") String patient,
      @RequestParam("code") String code,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    // PETERTODO
    return mrAndersonBundle(
        Parameters.builder()
            .add("patient", patient)
            .add("code", code)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  // PETERTODO not diagnostic-report specific
  private int totalRecordsCount(MultiValueMap<String, String> protectedParameters) {
    TypedQuery<Long> totalQuery =
        entityManager.createQuery(queryTotal(protectedParameters), Long.class);
    queryAddParameters(totalQuery, protectedParameters);
    int totalRecords = totalQuery.getSingleResult().intValue();
    log.error("total records: " + totalRecords);
    return totalRecords;
  }

  /** Validate Endpoint. */
  @PostMapping(
    value = "/$validate",
    consumes = {"application/json", "application/json+fhir", "application/fhir+json"}
  )
  public OperationOutcome validate(@RequestBody DiagnosticReport.Bundle bundle) {
    return Validator.create().validate(bundle);
  }

  public interface Transformer extends Function<CdwDiagnosticReport, DiagnosticReport> {}
}
