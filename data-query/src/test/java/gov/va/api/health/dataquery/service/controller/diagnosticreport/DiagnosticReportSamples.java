package gov.va.api.health.dataquery.service.controller.diagnosticreport;

import static gov.va.api.health.dataquery.service.controller.Transformers.parseInstant;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.ids.api.Registration;
import gov.va.api.health.ids.api.ResourceIdentity;
import gov.va.api.lighthouse.datamart.DatamartReference;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DiagnosticReportSamples {
  public static ResourceIdentity id(String cdwId) {
    return ResourceIdentity.builder()
        .system("CDW")
        .resource("DIAGNOSTIC_REPORT")
        .identifier(cdwId)
        .build();
  }

  @SneakyThrows
  static String json(Object o) {
    return JacksonConfig.createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
  }

  public static Registration registration(String cdwId, String publicId) {
    return Registration.builder().uuid(publicId).resourceIdentities(List.of(id(cdwId))).build();
  }

  @AllArgsConstructor(staticName = "create")
  public static class DatamartV2 {
    public Optional<DatamartReference> accessionInstitution() {
      String performer = "655775";
      String performerDisplay = "MANILA-RO";
      return Optional.of(referenceOf("Organization", performer, performerDisplay));
    }

    public DatamartDiagnosticReport diagnosticReport() {
      String icn = "1011537977V693883";
      String reportId = "800260864479:L";
      return diagnosticReport(reportId, icn);
    }

    public DatamartDiagnosticReport diagnosticReport(String cdwId, String patientIcn) {
      String issuedDateTime = "2009-09-24T03:36:35Z";
      return diagnosticReport(cdwId, patientIcn, issuedDateTime);
    }

    public DatamartDiagnosticReport diagnosticReport(
        String cdwId, String patientIcn, String issuedDate) {
      String effectiveDateTime = "2009-09-24T03:15:24Z";
      return DatamartDiagnosticReport.builder()
          .cdwId(cdwId)
          .patient(referenceOf("Patient", patientIcn, null))
          .accessionInstitution(accessionInstitution())
          .effectiveDateTime(effectiveDateTime)
          .issuedDateTime(issuedDate)
          .results(results())
          .build();
    }

    public DiagnosticReportEntity entity(String cdwId, String patientIcn) {
      return entity(diagnosticReport(cdwId, patientIcn));
    }

    @SneakyThrows
    public DiagnosticReportEntity entity(DatamartDiagnosticReport dm) {
      return DiagnosticReportEntity.builder()
          .cdwId(dm.cdwId())
          .icn(dm.patient().reference().orElse(null))
          .category("CH")
          .dateUtc(Instant.parse(dm.issuedDateTime()))
          .payload(json(dm))
          .build();
    }

    private DatamartReference referenceOf(String type, String id, String display) {
      return DatamartReference.builder()
          .type(Optional.of(type))
          .reference(Optional.ofNullable(id))
          .display(Optional.ofNullable(display))
          .build();
    }

    public List<DatamartReference> results() {
      return List.of(referenceOf("Observation", "TEST", "1234"));
    }
  }

  @Builder
  public static class Dstu2 {
    @Builder.Default String icn = "1011537977V693883";

    @Builder.Default String reportId = "800260864479:L";

    @Builder.Default String effectiveDateTime = "2009-09-24T03:15:24";

    @Builder.Default String issuedDateTime = "2009-09-24T03:36:35";

    @Builder.Default String performer = "655775";

    @Builder.Default String performerDisplay = "MANILA-RO";

    public static gov.va.api.health.dstu2.api.resources.DiagnosticReport.Bundle asBundle(
        String baseUrl,
        Collection<gov.va.api.health.dstu2.api.resources.DiagnosticReport> reports,
        int totalRecords,
        gov.va.api.health.dstu2.api.bundle.BundleLink... links) {
      return gov.va.api.health.dstu2.api.resources.DiagnosticReport.Bundle.builder()
          .resourceType("Bundle")
          .type(gov.va.api.health.dstu2.api.bundle.AbstractBundle.BundleType.searchset)
          .total(totalRecords)
          .link(Arrays.asList(links))
          .entry(
              reports.stream()
                  .map(
                      c ->
                          gov.va.api.health.dstu2.api.resources.DiagnosticReport.Entry.builder()
                              .fullUrl(baseUrl + "/DiagnosticReport/" + c.id())
                              .resource(c)
                              .search(
                                  gov.va.api.health.dstu2.api.bundle.AbstractEntry.Search.builder()
                                      .mode(
                                          gov.va.api.health.dstu2.api.bundle.AbstractEntry
                                              .SearchMode.match)
                                      .build())
                              .build())
                  .collect(Collectors.toList()))
          .build();
    }

    public static Dstu2 create() {
      return Dstu2.builder().build();
    }

    public static gov.va.api.health.dstu2.api.bundle.BundleLink link(
        gov.va.api.health.dstu2.api.bundle.BundleLink.LinkRelation rel,
        String base,
        int page,
        int count) {
      return gov.va.api.health.dstu2.api.bundle.BundleLink.builder()
          .relation(rel)
          .url(base + "&page=" + page + "&_count=" + count)
          .build();
    }

    public gov.va.api.health.dstu2.api.elements.Reference performer() {
      return gov.va.api.health.dstu2.api.elements.Reference.builder()
          .reference("Organization/" + performer)
          .display(performerDisplay)
          .build();
    }

    public gov.va.api.health.dstu2.api.resources.DiagnosticReport report(
        String publicId, String patientIcn) {
      return report(publicId, patientIcn, issuedDateTime);
    }

    public gov.va.api.health.dstu2.api.resources.DiagnosticReport report(
        String publicId, String patientIcn, String issuedDateTime) {
      return gov.va.api.health.dstu2.api.resources.DiagnosticReport.builder()
          .id(publicId)
          .resourceType("DiagnosticReport")
          .status(gov.va.api.health.dstu2.api.resources.DiagnosticReport.Code._final)
          .category(
              gov.va.api.health.dstu2.api.datatypes.CodeableConcept.builder()
                  .coding(
                      asList(
                          gov.va.api.health.dstu2.api.datatypes.Coding.builder()
                              .system("http://hl7.org/fhir/ValueSet/diagnostic-service-sections")
                              .code("LAB")
                              .display("Laboratory")
                              .build()))
                  .build())
          .code(
              gov.va.api.health.dstu2.api.datatypes.CodeableConcept.builder().text("panel").build())
          .subject(
              gov.va.api.health.dstu2.api.elements.Reference.builder()
                  .reference("Patient/" + patientIcn)
                  .build())
          .effectiveDateTime(parseInstant(effectiveDateTime).toString())
          .issued(parseInstant(issuedDateTime).toString())
          .result(
              List.of(
                  gov.va.api.health.dstu2.api.elements.Reference.builder()
                      .reference("Observation/TEST")
                      .display("1234")
                      .build()))
          .performer(performer())
          .build();
    }

    public gov.va.api.health.dstu2.api.resources.DiagnosticReport report(String publicId) {
      return report(publicId, icn);
    }

    public gov.va.api.health.dstu2.api.resources.DiagnosticReport report() {
      return report(reportId);
    }
  }

  @AllArgsConstructor(staticName = "create")
  public static class R4 {
    public static gov.va.api.health.r4.api.resources.DiagnosticReport.Bundle asBundle(
        String baseUrl,
        Collection<gov.va.api.health.r4.api.resources.DiagnosticReport> reports,
        int totalRecords,
        gov.va.api.health.r4.api.bundle.BundleLink... links) {
      return gov.va.api.health.r4.api.resources.DiagnosticReport.Bundle.builder()
          .resourceType("Bundle")
          .type(gov.va.api.health.r4.api.bundle.AbstractBundle.BundleType.searchset)
          .total(totalRecords)
          .link(Arrays.asList(links))
          .entry(
              reports.stream()
                  .map(
                      c ->
                          gov.va.api.health.r4.api.resources.DiagnosticReport.Entry.builder()
                              .fullUrl(baseUrl + "/DiagnosticReport/" + c.id())
                              .resource(c)
                              .search(
                                  gov.va.api.health.r4.api.bundle.AbstractEntry.Search.builder()
                                      .mode(
                                          gov.va.api.health.r4.api.bundle.AbstractEntry.SearchMode
                                              .match)
                                      .build())
                              .build())
                  .collect(Collectors.toList()))
          .build();
    }

    public static gov.va.api.health.r4.api.bundle.BundleLink link(
        gov.va.api.health.r4.api.bundle.BundleLink.LinkRelation rel,
        String base,
        int page,
        int count) {
      return gov.va.api.health.r4.api.bundle.BundleLink.builder()
          .relation(rel)
          .url(base + "&page=" + page + "&_count=" + count)
          .build();
    }

    public gov.va.api.health.r4.api.resources.DiagnosticReport diagnosticReport() {
      String reportId = "800260864479:L";
      return diagnosticReport(reportId);
    }

    public gov.va.api.health.r4.api.resources.DiagnosticReport diagnosticReport(String publicId) {
      String icn = "1011537977V693883";
      return diagnosticReport(publicId, icn);
    }

    public gov.va.api.health.r4.api.resources.DiagnosticReport diagnosticReport(
        String publicId, String icn) {
      String issuedDateTime = "2009-09-24T03:36:35Z";
      return diagnosticReport(publicId, icn, issuedDateTime);
    }

    public gov.va.api.health.r4.api.resources.DiagnosticReport diagnosticReport(
        String publicId, String patientIcn, String issuedDateTime) {
      String effectiveDateTime = "2009-09-24T03:15:24Z";
      return gov.va.api.health.r4.api.resources.DiagnosticReport.builder()
          .id(publicId)
          .resourceType("DiagnosticReport")
          .status(gov.va.api.health.r4.api.resources.DiagnosticReport.DiagnosticReportStatus._final)
          .category(
              singletonList(
                  gov.va.api.health.r4.api.datatypes.CodeableConcept.builder()
                      .coding(
                          singletonList(
                              gov.va.api.health.r4.api.datatypes.Coding.builder()
                                  .system("http://terminology.hl7.org/CodeSystem/v2-0074")
                                  .code("LAB")
                                  .display("Laboratory")
                                  .build()))
                      .build()))
          .code(gov.va.api.health.r4.api.datatypes.CodeableConcept.builder().text("panel").build())
          .subject(
              gov.va.api.health.r4.api.elements.Reference.builder()
                  .reference("Patient/" + patientIcn)
                  .build())
          .effectiveDateTime(effectiveDateTime)
          .issued(issuedDateTime)
          .result(results())
          .build();
    }

    public List<gov.va.api.health.r4.api.elements.Reference> results() {
      return singletonList(
          gov.va.api.health.r4.api.elements.Reference.builder()
              .reference("Observation/TEST")
              .display("1234")
              .build());
    }
  }
}
