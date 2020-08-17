package gov.va.api.health.dataquery.service.controller.location;

import gov.va.api.health.r4.api.datatypes.Address;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.ContactPoint;
import gov.va.api.health.uscorer4.api.resources.Location;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class R4LocationTransformerTest {
    // ToDo samples and full transform

    @Test
    void status() {
        assertThat(tx().status(null)).isNull();
        assertThat(tx().status(DatamartLocation.Status.active)).isEqualTo(Location.Status.active);
        assertThat(tx().status(DatamartLocation.Status.inactive)).isEqualTo(Location.Status.inactive);
    }

    @Test
    void types() {
        assertThat(tx().types(Optional.empty())).isNull();
        assertThat(tx().types(Optional.of(" "))).isNull();
        assertThat(tx().types(Optional.of("x"))).isEqualTo(List.of(CodeableConcept.builder().coding(List.of(Coding.builder().display("x").build())).build()));
    }

    @Test
    void telecoms() {
        assertThat(tx().telecoms(null)).isNull();
        assertThat(tx().telecoms(" ")).isNull();
        assertThat(tx().telecoms("x")).isEqualTo(List.of(ContactPoint.builder().system(ContactPoint.ContactPointSystem.phone).value("x").build()));
    }

    @Test
    void physicalType() {
        assertThat(tx().physicalType(Optional.empty())).isNull();
        assertThat(tx().physicalType(Optional.of(" "))).isNull();
        assertThat(tx().physicalType(Optional.of("x"))).isEqualTo(CodeableConcept.builder().coding(List.of(Coding.builder().display("x").build())).build());
    }

    @Test
    void empty() {
        assertThat(tx().toFhir())
                .isEqualTo(Location.builder()
                        .resourceType("Location")
                        .mode(Location.Mode.instance)
                        .build());
    }

    @Test
    void address() {
        assertThat(tx().address(null)).isNull();
        assertThat(tx().address(DatamartLocation.Address.builder().build())).isNull();
        assertThat(tx().address(DatamartLocation.Address.builder().line1("w").build()))
                .isEqualTo(Address.builder().line(List.of("w")).text("w").build());
        assertThat(tx().address(DatamartLocation.Address.builder().city("x").build()))
                .isEqualTo(Address.builder().city("x").text("x").build());
        assertThat(tx().address(DatamartLocation.Address.builder().state("y").build()))
                .isEqualTo(Address.builder().state("y").text("y").build());
        assertThat(tx().address(DatamartLocation.Address.builder().postalCode("z").build()))
                .isEqualTo(Address.builder().postalCode("z").text("z").build());
        assertThat(tx().address(DatamartLocation.Address.builder().line1("w").postalCode("z").build()))
                .isEqualTo(Address.builder().line(List.of("w")).postalCode("z").text("w z").build());
        assertThat(tx().address(DatamartLocation.Address.builder().line1("w").city("x").state("y").postalCode("z").build()))
                .isEqualTo(Address.builder().line(List.of("w")).city("x").state("y").postalCode("z").text("w x y z").build());
    }

    private R4LocationTransformer tx() {
        return R4LocationTransformer.builder().datamart(DatamartLocation.builder().build()).build();
    }


}
