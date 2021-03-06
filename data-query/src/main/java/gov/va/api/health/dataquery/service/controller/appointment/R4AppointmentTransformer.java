package gov.va.api.health.dataquery.service.controller.appointment;

import static gov.va.api.health.dataquery.service.controller.Transformers.asDateTimeString;
import static gov.va.api.health.dataquery.service.controller.Transformers.isBlank;

import gov.va.api.health.dataquery.service.controller.R4Transformers;
import gov.va.api.health.r4.api.datatypes.CodeableConcept;
import gov.va.api.health.r4.api.datatypes.Coding;
import gov.va.api.health.r4.api.elements.Meta;
import gov.va.api.health.r4.api.resources.Appointment;
import gov.va.api.lighthouse.datamart.CompositeCdwId;
import gov.va.api.lighthouse.datamart.DatamartReference;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
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
                    .system("http://www.va.gov/Terminology/VistADefinedTerms/2.98-9.5")
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
                    .system("http://www.va.gov/Terminology/VistADefinedTerms/2.98-16")
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

  Meta meta(Instant dmLastUpdated) {
    String lastUpdated = asDateTimeString(dmLastUpdated);
    if (lastUpdated == null) {
      return null;
    }
    return Meta.builder().lastUpdated(lastUpdated).build();
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

  List<CodeableConcept> serviceCategory(Optional<String> maybeServiceCategory) {
    if (isBlank(maybeServiceCategory)) {
      return null;
    }
    String code = serviceCategoryCode(maybeServiceCategory);
    if (code == null) {
      return null;
    }
    String display = maybeServiceCategory.get();
    return List.of(
        CodeableConcept.builder()
            .coding(
                List.of(
                    Coding.builder()
                        .system("http://www.va.gov/Terminology/VistADefinedTerms/44-9")
                        .display(display)
                        .code(code)
                        .build()))
            .text(display)
            .build());
  }

  String serviceCategoryCode(Optional<String> maybeDisplay) {
    if (isBlank(maybeDisplay)) {
      return null;
    }
    String display = maybeDisplay.get().toUpperCase(Locale.ENGLISH).trim();
    switch (display) {
      case "MEDICINE":
        return "M";
      case "NEUROLOGY":
        return "N";
      case "NONE":
        return "0";
      case "PSYCHIATRY":
        return "P";
      case "REHAB MEDICINE":
        return "R";
      case "SURGERY":
        return "S";
      default:
        log.warn(
            "Appointment {} service-category '{}' cannot be mapped to code", dm.cdwId(), display);
        return null;
    }
  }

  List<CodeableConcept> serviceType(String serviceType) {
    if (isBlank(serviceType)) {
      return null;
    }
    return List.of(CodeableConcept.builder().text(serviceType).build());
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
   *   <li>"INPATIENT APPOINTMENT" -> use scheduled times and visit ID, either noshow, booked,
   *       fulfilled
   *   <li>"CANCELLED BY PATIENT" -> cancelled
   *   <li>"CANCELLED BY PATIENT & AUTO-REBOOK" -> cancelled
   *   <li>"NO ACTION TAKEN" -> use scheduled times and visit ID, either noshow, booked, fulfilled
   *   <li>null -> use scheduled times and visit ID, either noshow, booked, fulfilled
   * </ul>
   */
  Appointment.AppointmentStatus status(
      Optional<Instant> start, Optional<String> status, Optional<Long> visitSid) {
    if (isWaitlist()) {
      return Appointment.AppointmentStatus.waitlist;
    }
    switch (status.orElse("NO ACTION TAKEN")) {
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
        return statusFromStartAndVisitSid(start, visitSid);
      default:
        return Appointment.AppointmentStatus.booked;
    }
  }

  Appointment.AppointmentStatus statusFromStartAndVisitSid(
      Optional<Instant> start, Optional<Long> visitSid) {
    if (start.isEmpty() || start.get().isAfter(Instant.now())) {
      return Appointment.AppointmentStatus.booked;
    }
    if (visitSid.isPresent()) {
      if (visitSid.get() <= 0) {
        return Appointment.AppointmentStatus.noshow;
      }
      return Appointment.AppointmentStatus.fulfilled;
    }
    return Appointment.AppointmentStatus.booked;
  }

  Appointment toFhir() {
    return Appointment.builder()
        .id(dm.cdwId())
        .meta(meta(dm.lastUpdated()))
        .status(status(dm.start(), dm.status(), dm.visitSid()))
        .cancelationReason(cancelationReason(dm.cancelationReason()))
        .serviceCategory(serviceCategory(dm.serviceCategory()))
        .serviceType(serviceType(dm.serviceType()))
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
