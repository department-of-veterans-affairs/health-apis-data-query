package gov.va.api.health.dataquery.service.controller.location;

import static gov.va.api.health.dataquery.service.controller.FacilityTransformers.fapiClinicId;
import static gov.va.api.health.dataquery.service.controller.R4Transformers.asReference;
import static gov.va.api.health.dataquery.service.controller.Transformers.allBlank;
import static gov.va.api.health.dataquery.service.controller.Transformers.convert;
import static gov.va.api.health.dataquery.service.controller.Transformers.emptyToNull;
import static gov.va.api.health.dataquery.service.controller.Transformers.isBlank;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import gov.va.api.health.dataquery.service.controller.EnumSearcher;
import gov.va.api.health.dataquery.service.controller.FacilityId;
import gov.va.api.health.dataquery.service.controller.FacilityTransformers;
import gov.va.api.health.r4.api.datatypes.Address;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.datatypes.ContactPoint;
import gov.va.api.health.r4.api.datatypes.Identifier;
import gov.va.api.health.r4.api.resources.Location;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

/** Convert datamart to R4. */
@Builder
public class R4LocationTransformer {
  @NonNull private final DatamartLocation datamart;

  Address address(DatamartLocation.Address address) {
    if (address == null
        || allBlank(address.line1(), address.city(), address.state(), address.postalCode())) {
      return null;
    }
    String addressText =
        Stream.of(address.line1(), address.city(), address.state(), address.postalCode())
            .map(StringUtils::trimToNull)
            .filter(Objects::nonNull)
            .collect(Collectors.joining(" "));
    return Address.builder()
        .line(emptyToNull(singletonList(address.line1())))
        .city(address.city())
        .state(address.state())
        .postalCode(address.postalCode())
        .text(addressText)
        .build();
  }

  Identifier clinicIdentifier(FacilityId facilityId, String clinicId) {
    String fapiClinicId = fapiClinicId(facilityId, clinicId);
    if (isBlank(fapiClinicId)) {
      return null;
    }
    return Identifier.builder()
        .system(FacilityTransformers.FAPI_CLINIC_IDENTIFIER_SYSTEM)
        .value(fapiClinicId)
        .build();
  }

  List<Identifier> identifiers(Optional<FacilityId> maybeFacilityId, Optional<String> maybeIen) {
    return emptyToNull(
        asList(clinicIdentifier(maybeFacilityId.orElse(null), maybeIen.orElse(null))));
  }

  CodeableConcept physicalType(Optional<String> maybePhysicalType) {
    if (isBlank(maybePhysicalType)) {
      return null;
    }
    return CodeableConcept.builder()
        .coding(singletonList(Coding.builder().display(maybePhysicalType.get()).build()))
        .build();
  }

  Location.Status status(DatamartLocation.Status status) {
    return convert(status, s -> EnumSearcher.of(Location.Status.class).find(s.toString()));
  }

  List<ContactPoint> telecoms(String telecom) {
    if (isBlank(telecom)) {
      return null;
    }
    return List.of(
        ContactPoint.builder()
            .system(ContactPoint.ContactPointSystem.phone)
            .value(telecom)
            .build());
  }

  Location toFhir() {
    return Location.builder()
        .id(datamart.cdwId())
        .identifier(identifiers(datamart.facilityId(), datamart.locationIen()))
        .status(status(datamart.status()))
        .name(datamart.name())
        .description(datamart.description().orElse(null))
        .mode(Location.Mode.instance)
        .type(types(datamart.type()))
        .telecom(telecoms(datamart.telecom()))
        .address(address(datamart.address()))
        .physicalType(physicalType(datamart.physicalType()))
        .managingOrganization(asReference(datamart.managingOrganization()))
        .build();
  }

  List<CodeableConcept> types(Optional<String> maybeType) {
    if (isBlank(maybeType)) {
      return null;
    }
    return List.of(
        CodeableConcept.builder()
            .coding(List.of(Coding.builder().display(maybeType.get()).build()))
            .build());
  }
}
