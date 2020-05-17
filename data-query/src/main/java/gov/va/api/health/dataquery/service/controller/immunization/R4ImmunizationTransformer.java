package gov.va.api.health.dataquery.service.controller.immunization;

import static gov.va.api.health.dataquery.service.controller.R4Transformers.asReference;
import static gov.va.api.health.dataquery.service.controller.Transformers.asDateTimeString;

import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import gov.va.api.health.r4.api.datatypes.Annotation;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.uscorer4.api.resources.Immunization;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class R4ImmunizationTransformer {
  @NonNull final DatamartImmunization datamart;

  static List<Annotation> note(Optional<String> note) {
    return note.isPresent() ? List.of(Annotation.builder().text(note.get()).build()) : null;
  }

  static List<Immunization.Performer> performer(Optional<DatamartReference> maybePerformer) {
    if (maybePerformer == null || !maybePerformer.isPresent()) {
      return null;
    }
    return List.of(Immunization.Performer.builder().actor(asReference(maybePerformer)).build());
  }

  static List<Immunization.Reaction> reaction(Optional<DatamartReference> reaction) {
    return (reaction.isPresent() && reaction.get().hasDisplayOrTypeAndReference())
        ? List.of(Immunization.Reaction.builder().detail(asReference(reaction)).build())
        : null;
  }

  static Immunization.Status status(DatamartImmunization.Status status) {
    if (status == null) {
      return null;
    }
    switch (status) {
      case completed:
        return Immunization.Status.completed;
      case entered_in_error:
        return Immunization.Status.entered_in_error;
      case not_done:
        return Immunization.Status.not_done;
      case data_absent_reason_unsupported:
        /* For unsupported, we'll need to set the _status extension field */
        return null;
      default:
        throw new IllegalArgumentException("Unsupported status: " + status);
    }
  }

  static CodeableConcept vaccineCode(DatamartImmunization.VaccineCode vaccineCode) {
    if (vaccineCode == null) {
      return null;
    }
    return CodeableConcept.builder()
        .text(vaccineCode.text())
        .coding(
            List.of(
                Coding.builder()
                    .system("http://hl7.org/fhir/sid/cvx")
                    .code(vaccineCode.code())
                    .build()))
        .build();
  }

  Immunization toFhir() {
    return Immunization.builder()
        .resourceType(Immunization.class.getSimpleName())
        .id(datamart.cdwId())
        .status(status(datamart.status()))
        // ._status() DAR in R4?
        .occurrenceDateTime(asDateTimeString(datamart.date()))
        .vaccineCode(vaccineCode(datamart.vaccineCode()))
        .patient(asReference(datamart.patient()))
        .performer(performer(datamart.performer()))
        .location(asReference(datamart.location()))
        .note(note(datamart.note()))
        .reaction(reaction(datamart.reaction()))
        .build();
  }
}
