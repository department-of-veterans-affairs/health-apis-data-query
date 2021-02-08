package gov.va.api.health.dataquery.service.controller.appointment;

import static gov.va.api.health.dataquery.service.controller.Transformers.allBlank;
import static gov.va.api.health.dataquery.service.controller.Transformers.asDateTimeString;
import static gov.va.api.health.dataquery.service.controller.Transformers.isBlank;

import gov.va.api.health.dataquery.service.controller.R4Transformers;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.resources.Appointment;
import gov.va.api.lighthouse.datamart.CompositeCdwId;
import gov.va.api.lighthouse.datamart.DatamartReference;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Builder
final class R4AppointmentTransformer {
  private static final Set<String> SUPPORTED_PARTICIPANT_TYPES = Set.of("Location", "Patient");

  @NonNull private final DatamartAppointment dm;

  @NonNull private final CompositeCdwId compositeCdwId;

  CodeableConcept appointmentType(Optional<String> maybeAppointmentType) {
    if (isBlank(maybeAppointmentType)) {
      return null;
    }
    return CodeableConcept.builder()
        .coding(
            List.of(
                Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v2-0276")
                    .display(maybeAppointmentType.get())
                    .build()))
        .text(maybeAppointmentType.get())
        .build();
  }

  CodeableConcept cancelationReason(Optional<String> maybeCancelationReason) {
    if (isBlank(maybeCancelationReason)) {
      return null;
    }
    return CodeableConcept.builder()
        .coding(
            List.of(
                Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/appointment-cancellation-reason")
                    .display(maybeCancelationReason.get())
                    .build()))
        .text(maybeCancelationReason.get())
        .build();
  }

  String comment(Optional<String> maybeComment) {
    if (isBlank(maybeComment)) {
      return null;
    }
    return maybeComment.get();
  }

  String description(Optional<String> maybeDescription) {
    if (isBlank(maybeDescription)) {
      return null;
    }
    return maybeDescription.get();
  }

  boolean isAppointment() {
    return compositeCdwId.cdwIdResourceCode() == 'A';
  }

  boolean isSupportedParticipant(DatamartReference r) {
    return !isBlank(r)
        && r.hasTypeAndReference()
        && SUPPORTED_PARTICIPANT_TYPES.contains(r.type().get());
  }

  boolean isWaitlist() {
    return compositeCdwId.cdwIdResourceCode() == 'W';
  }

  Integer minutesDuration(Optional<Integer> maybeMinutesDuration) {
    if (isBlank(maybeMinutesDuration)) {
      return null;
    }
    return maybeMinutesDuration.get();
  }

  Appointment.Participant participant(
      DatamartReference dmReference, Appointment.ParticipationStatus participationStatus) {
    if (isBlank(dmReference)) {
      return null;
    }
    // We only understand Appointment Participants that are Locations or Patients at this time.
    if (!isSupportedParticipant(dmReference)) {
      return null;
    }
    return Appointment.Participant.builder()
        .actor(R4Transformers.asReference(dmReference))
        .status(participationStatus)
        .build();
  }

  List<Appointment.Participant> participants(List<DatamartReference> dmParticipants) {
    if (isBlank(compositeCdwId.cdwIdResourceCode())) {
      return null;
    }
    if (isBlank(dmParticipants)) {
      return null;
    }
    // If the appointment is from the WAITLIST TABLE(cdwId = 123456:W) status is tentative
    // If the appointment is from the APPOINTMENT TABLE(cdwId = 123456:A) status is accepted
    Appointment.ParticipationStatus participationStatus;
    if (isWaitlist()) {
      participationStatus = Appointment.ParticipationStatus.tentative;
    } else if (isAppointment()) {
      participationStatus = Appointment.ParticipationStatus.accepted;
    } else {
      return null;
    }
    return dmParticipants.stream()
        .map(p -> participant(p, participationStatus))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  List<CodeableConcept> specialty(Optional<String> maybeSpecialty) {
    if (isBlank(maybeSpecialty)) {
      return null;
    }
    return List.of(
        CodeableConcept.builder()
            .coding(
                List.of(
                    Coding.builder()
                        .system("http://hl7.org/fhir/ValueSet/c80-practice-codes")
                        .display(maybeSpecialty.get())
                        .build()))
            .text(maybeSpecialty.get())
            .build());
  }

  /**
   * Business Logic Rules for determining status from CDW's datamart.status String.
   *
   * <ul>
   *   <li>cdwId resource code = 'W' -> waitlist
   *   <li>"NO SHOW" -> noshow
   *   <li>"CANCELLED BY CLINIC" -> cancelled
   *   <li>"NO-SHOW & AUTO RE-BOOK" -> noshow
   *   <li>"CANCELLED BY CLINIC & AUTO RE-BOOK" -> cancelled
   *   <li>"INPATIENT APPOINTMENT" -> use checkin times, either arrived, booked, fulfilled
   *   <li>"CANCELLED BY PATIENT" -> cancelled
   *   <li>"CANCELLED BY PATIENT & AUTO-REBOOK" -> cancelled
   *   <li>"NO ACTION TAKEN" -> use checkin times, either arrived, booked, fulfilled
   *   <li>null -> use checkin times, either arrived, booked, fulfilled
   * </ul>
   */
  Appointment.AppointmentStatus status(
      Optional<Instant> start, Optional<Instant> end, Optional<String> status) {
    if (isBlank(compositeCdwId.cdwIdResourceCode())) {
      return null;
    }
    if (allBlank(end, status)) {
      return null;
    }
    if (isWaitlist()) {
      return Appointment.AppointmentStatus.waitlist;
    }
    if (status.isEmpty()) {
      return statusFromStartAndEndTime(start, end);
    }
    switch (status.get()) {
      case "NO SHOW":
      case "NO-SHOW & AUTO RE-BOOK":
        return Appointment.AppointmentStatus.noshow;
      case "CANCELLED BY PATIENT":
      case "CANCELLED BY PATIENT & AUTO-REBOOK":
      case "CANCELLED BY CLINIC":
      case "CANCELLED BY CLINIC & AUTO RE-BOOK":
        return Appointment.AppointmentStatus.cancelled;
      case "INPATIENT APPOINTMENT":
      case "NO ACTION TAKEN":
        return statusFromStartAndEndTime(start, end);
      default:
        return null;
    }
  }

  Appointment.AppointmentStatus statusFromStartAndEndTime(
      Optional<Instant> start, Optional<Instant> end) {
    if (start.isEmpty() && end.isEmpty()) {
      return Appointment.AppointmentStatus.booked;
    }
    if (start.isPresent() && end.isEmpty()) {
      return Appointment.AppointmentStatus.arrived;
    }
    if (start.isPresent() && end.isPresent()) {
      return Appointment.AppointmentStatus.fulfilled;
    }
    return null;
  }

  Appointment toFhir() {
    return Appointment.builder()
        .resourceType("Appointment")
        .id(dm.cdwId())
        .status(status(dm.start(), dm.end(), dm.status()))
        .cancelationReason(cancelationReason(dm.cancelationReason()))
        .specialty(specialty(dm.specialty()))
        .appointmentType(appointmentType(dm.appointmentType()))
        .description(description(dm.description()))
        .start(asDateTimeString(dm.start()))
        .end(asDateTimeString(dm.end()))
        .minutesDuration(minutesDuration(dm.minutesDuration()))
        .created(dm.created())
        .comment(comment(dm.comment()))
        .participant(participants(dm.participant()))
        .build();
  }
}
