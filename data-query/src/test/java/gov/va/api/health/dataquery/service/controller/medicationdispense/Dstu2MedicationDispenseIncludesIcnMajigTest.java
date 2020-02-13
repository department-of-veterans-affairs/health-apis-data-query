package gov.va.api.health.dataquery.service.controller.medicationdispense;

import gov.va.api.health.dataquery.service.controller.ExtractIcnValidator;
import gov.va.api.health.dstu2.api.elements.Reference;
import gov.va.api.health.dstu2.api.resources.MedicationDispense;
import java.util.List;
import org.junit.Test;

public class Dstu2MedicationDispenseIncludesIcnMajigTest {
  @Test
  public void extractIcn() {
    ExtractIcnValidator.builder()
        .majig(new Dstu2MedicationDispenseIncludesIcnMajig())
        .body(
            MedicationDispense.builder()
                .id("123")
                .patient(Reference.builder().reference("Patient/1010101010V666666").build())
                .build())
        .expectedIcns(List.of("1010101010V666666"))
        .build()
        .assertIcn();
  }
}
