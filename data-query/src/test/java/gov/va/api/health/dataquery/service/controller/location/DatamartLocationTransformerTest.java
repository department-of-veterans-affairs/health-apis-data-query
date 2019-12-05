package gov.va.api.health.dataquery.service.controller.location;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import gov.va.api.health.dstu2.api.datatypes.Address;
import gov.va.api.health.dstu2.api.datatypes.CodeableConcept;
import gov.va.api.health.dstu2.api.datatypes.Coding;
import gov.va.api.health.dstu2.api.datatypes.ContactPoint;
import gov.va.api.health.dstu2.api.resources.Location;

public class DatamartLocationTransformerTest {
  @Test
  public void address() {
    assertThat(DatamartLocationTransformer.address(null)).isNull();
    assertThat(
            DatamartLocationTransformer.address(
                DatamartLocation.Address.builder().line1(" ").city("x").build()))
        .isNull();
    assertThat(
            DatamartLocationTransformer.address(
                DatamartLocation.Address.builder().city(" ").state(" ").postalCode(" ").build()))
        .isNull();
    assertThat(
            DatamartLocationTransformer.address(
                DatamartLocation.Address.builder()
                    .line1("w")
                    .city("x")
                    .state("y")
                    .postalCode("z")
                    .build()))
        .isEqualTo(
            Address.builder().line(asList("w")).city("x").state("y").postalCode("z").build());
  }

  @Test
  public void phsyicalType() {
    assertThat(DatamartLocationTransformer.physicalType(Optional.empty())).isNull();
    assertThat(DatamartLocationTransformer.physicalType(Optional.of(" "))).isNull();
    assertThat(DatamartLocationTransformer.physicalType(Optional.of("x")))
        .isEqualTo(
            CodeableConcept.builder()
                .coding(asList(Coding.builder().display("x").build()))
                .build());
  }

  @Test
  public void status() {
    assertThat(DatamartLocationTransformer.status(null)).isNull();
    assertThat(DatamartLocationTransformer.status(DatamartLocation.Status.active))
        .isEqualTo(Location.Status.active);
    assertThat(DatamartLocationTransformer.status(DatamartLocation.Status.inactive))
        .isEqualTo(Location.Status.inactive);
  }

  @Test
  public void telecoms() {
    assertThat(DatamartLocationTransformer.telecoms(" ")).isNull();
    assertThat(DatamartLocationTransformer.telecoms("x"))
        .isEqualTo(
            asList(
                ContactPoint.builder()
                    .system(ContactPoint.ContactPointSystem.phone)
                    .value("x")
                    .build()));
  }

  @Test
  public void type() {
    assertThat(DatamartLocationTransformer.type(Optional.empty())).isNull();
    assertThat(DatamartLocationTransformer.type(Optional.of(" "))).isNull();
    assertThat(DatamartLocationTransformer.type(Optional.of("x")))
        .isEqualTo(
            CodeableConcept.builder()
                .coding(asList(Coding.builder().display("x").build()))
                .build());
  }
}
