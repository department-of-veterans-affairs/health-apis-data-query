package gov.va.api.health.dataquery.service.controller.practitioner;

import gov.va.api.health.dataquery.service.controller.ExtractIcnValidator;
import gov.va.api.health.r4.api.resources.Practitioner;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PractitionerIncludesIcnMajigTest {
  @Test
  void dstu2() {
    ExtractIcnValidator.builder()
        .majig(new Dstu2PractitionerIncludesIcnMajig())
        .body(gov.va.api.health.dstu2.api.resources.Practitioner.builder().id("123").build())
        .expectedIcns(List.of("NONE"))
        .build()
        .assertIcn();
  }

  @Test
  void r4() {
    ExtractIcnValidator.builder()
        .majig(new R4PractitionerIncludesIcnMajig())
        .body(Practitioner.builder().id("123").build())
        .expectedIcns(List.of("NONE"))
        .build()
        .assertIcn();
  }

  @Test
  public void stu3() {
    ExtractIcnValidator.builder()
        .majig(new Stu3PractitionerIncludesIcnMajig())
        .body(gov.va.api.health.stu3.api.resources.Practitioner.builder().id("123").build())
        .expectedIcns(List.of("NONE"))
        .build()
        .assertIcn();
  }
}
