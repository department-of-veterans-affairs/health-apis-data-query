package gov.va.api.health.dataquery.tests.r4;

import gov.va.api.health.dataquery.tests.DataQueryResourceVerifier;
import gov.va.api.health.dataquery.tests.TestIds;
import gov.va.api.health.fhir.testsupport.ResourceVerifier;
import gov.va.api.health.r4.api.resources.OperationOutcome;
import gov.va.api.health.r4.api.resources.PractitionerRole;
import java.util.function.Predicate;
import lombok.experimental.Delegate;
import org.junit.jupiter.api.Test;

public class PractitionerRoleIT {
  @Delegate ResourceVerifier verifier = DataQueryResourceVerifier.r4();

  TestIds testIds = DataQueryResourceVerifier.ids();

  static Predicate<PractitionerRole.Bundle> bundleHasResults() {
    return bundleIsEmpty().negate();
  }

  static Predicate<PractitionerRole.Bundle> bundleIsEmpty() {
    return bundle -> bundle.entry().isEmpty();
  }

  @Test
  void read() {
    verifier.verifyAll(
        test(200, PractitionerRole.class, "PractitionerRole/{id}", testIds.practitionerRole()),
        test(404, OperationOutcome.class, "PractitionerRole/{unknown}", testIds.unknown()));
  }

  @Test
  void searchById() {
    verifyAll(
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleHasResults(),
            "PractitionerRole?_id={id}",
            testIds.practitionerRole()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?_id={unknown}",
            testIds.unknown()));
  }

  @Test
  void searchByName() {
    // Grab subset of family/given names for startsWith tests
    String startFamily = testIds.practitioners().family().substring(0, 3);
    String startGiven = testIds.practitioners().given().substring(0, 3);
    verifier.verifyAll(
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleHasResults(),
            "PractitionerRole?practitioner.name={given}",
            testIds.practitioners().given()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleHasResults(),
            "PractitionerRole?practitioner.name={family}",
            testIds.practitioners().family()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleHasResults(),
            "PractitionerRole?practitioner.name:exact={family}",
            testIds.practitioners().family()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleHasResults(),
            "PractitionerRole?practitioner.name={given}",
            startGiven),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleHasResults(),
            "PractitionerRole?practitioner.name={family}",
            startFamily),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?practitioner.name={given}",
            testIds.unknown()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?practitioner.name={family}",
            testIds.unknown()));
  }

  @Test
  void searchByPractitionerIdentifier() {
    verifyAll(
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleHasResults(),
            "PractitionerRole?practitioner.identifier={id}",
            testIds.practitioner()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleHasResults(),
            "PractitionerRole?practitioner.identifier={npi}",
            testIds.practitioners().npi()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?practitioner.identifier={unknown}",
            testIds.unknown()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?practitioner.identifier=http://hl7.org/fhir/sid/us-npi|"),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleHasResults(),
            "PractitionerRole?practitioner.identifier=http://hl7.org/fhir/sid/us-npi|{npi}",
            testIds.practitioners().npi()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?practitioner.identifier=http://hl7.org/fhir/sid/us-npi|{id}",
            testIds.practitioner()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?practitioner.identifier=http://hl7.org/fhir/sid/us-npi|{unknown}",
            testIds.unknown()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?practitioner.identifier=unknown-system|{npi}",
            testIds.practitioners().npi()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?practitioner.identifier=|{npi}",
            testIds.practitioners().npi()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?practitioner.identifier=|{unknown}",
            testIds.unknown()));
  }

  @Test
  void searchBySpecialty() {
    verifyAll(
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleHasResults(),
            "PractitionerRole?specialty={code}",
            testIds.practitioners().specialty()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?specialty={unknown}",
            testIds.unknown()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?specialty=http://nucc.org/provider-taxonomy|"),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleHasResults(),
            "PractitionerRole?specialty=http://nucc.org/provider-taxonomy|{code}",
            testIds.practitioners().specialty()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?specialty=http://nucc.org/provider-taxonomy|{unknown}",
            testIds.unknown()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?specialty=unknown-system|{code}",
            testIds.practitioners().specialty()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?specialty=|{code}",
            testIds.practitioners().specialty()),
        test(
            200,
            PractitionerRole.Bundle.class,
            bundleIsEmpty(),
            "PractitionerRole?specialty=|{unknown}",
            testIds.unknown()));
  }
}
