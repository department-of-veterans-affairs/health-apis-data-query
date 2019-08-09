package gov.va.api.health.dataquery.service.controller.medication;

import gov.va.api.health.argonaut.api.resources.Medication;
import gov.va.api.health.dstu2.api.bundle.AbstractBundle;
import gov.va.api.health.dstu2.api.bundle.AbstractEntry;
import gov.va.api.health.dstu2.api.bundle.BundleLink;
import gov.va.api.health.dstu2.api.datatypes.CodeableConcept;
import gov.va.api.health.dstu2.api.datatypes.Coding;
import gov.va.api.health.dstu2.api.elements.Narrative;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DatamartMedicationSamples {

  @AllArgsConstructor(staticName = "create")
  static class Datamart {

    DatamartMedication medication() {
      return medication("1000");
    }

    DatamartMedication medication(String cdwId) {
      return DatamartMedication.builder()
          .objectType("Medication")
          .objectVersion("1")
          .cdwId(cdwId)
          .rxnorm(rxNorm())
          .product(product())
          .build();
    }

    DatamartMedication.Product product() {
      return DatamartMedication.Product.builder().id("4015523").formText("TAB").build();
    }

    DatamartMedication.RxNorm rxNorm() {
      return DatamartMedication.RxNorm.builder()
          .code("284205")
          .text("ALMOTRIPTAN MALATE 12.5MG TAB,UD")
          .build();
    }
  }

  @AllArgsConstructor(staticName = "create")
  static class Fhir {

    static Medication.Bundle asBundle(
        String baseUrl, Collection<Medication> medications, BundleLink... links) {
      return Medication.Bundle.builder()
          .resourceType("Bundle")
          .type(AbstractBundle.BundleType.searchset)
          .total(medications.size())
          .link(Arrays.asList(links))
          .entry(
              medications
                  .stream()
                  .map(
                      c ->
                          Medication.Entry.builder()
                              .fullUrl(baseUrl + "/Medication/" + c.id())
                              .resource(c)
                              .search(
                                  AbstractEntry.Search.builder()
                                      .mode(AbstractEntry.SearchMode.match)
                                      .build())
                              .build())
                  .collect(Collectors.toList()))
          .build();
    }

    static BundleLink link(BundleLink.LinkRelation rel, String base, int page, int count) {
      return BundleLink.builder()
          .relation(rel)
          .url(base + "&page=" + page + "&_count=" + count)
          .build();
    }

    CodeableConcept code() {
      return CodeableConcept.builder()
          .coding(
              List.of(
                  Coding.builder()
                      .system("https://www.nlm.nih.gov/research/umls/rxnorm/")
                      .code("284205")
                      .display("ALMOTRIPTAN MALATE 12.5MG TAB,UD")
                      .build()))
          .text("ALMOTRIPTAN MALATE 12.5MG TAB,UD")
          .build();
    }

    Medication medication() {
      return medication("1000");
    }

    Medication medication(String id) {
      return Medication.builder()
          .resourceType(Medication.class.getSimpleName())
          .id(id)
          .code(code())
          .product(product())
          .text(text())
          .build();
    }

    Medication.Product product() {
      return Medication.Product.builder()
          .id("4015523")
          .form(CodeableConcept.builder().text("TAB").build())
          .build();
    }

    Narrative text() {
      return Narrative.builder()
          .status(Narrative.NarrativeStatus.additional)
          .div("<div>ALMOTRIPTAN MALATE 12.5MG TAB,UD</div>")
          .build();
    }
  }
}
