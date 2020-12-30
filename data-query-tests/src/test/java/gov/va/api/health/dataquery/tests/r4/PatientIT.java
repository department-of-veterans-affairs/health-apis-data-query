package gov.va.api.health.dataquery.tests.r4;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentIn;
import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;

import gov.va.api.health.dataquery.tests.ResourceVerifier;
import gov.va.api.health.r4.api.resources.OperationOutcome;
import gov.va.api.health.r4.api.resources.Patient;
import gov.va.api.health.sentinel.Environment;
import lombok.experimental.Delegate;
import org.junit.jupiter.api.Test;

public class PatientIT {
  @Delegate ResourceVerifier verifier = ResourceVerifier.r4();

  @Test
  public void advanced() {
    assumeEnvironmentIn(Environment.LOCAL);
    verifier.verifyAll(
        test(
            200,
            Patient.Bundle.class,
            "Patient?family={family}&gender={gender}",
            verifier.ids().pii().family(),
            verifier.ids().pii().gender()),
        test(
            200,
            Patient.Bundle.class,
            "Patient?name={name}",
            verifier.ids().pii().name().replaceAll("\\s", "")),
        test(
            200,
            Patient.Bundle.class,
            "Patient?name={name}&birthdate={birthdate}",
            verifier.ids().pii().name().replaceAll("\\s", ""),
            verifier.ids().pii().birthdate()),
        test(
            200,
            Patient.Bundle.class,
            "Patient?family={family}&birthdate={birthdate}",
            verifier.ids().pii().family(),
            verifier.ids().pii().birthdate()),
        test(
            200,
            Patient.Bundle.class,
            "Patient?name={name}&gender={gender}",
            verifier.ids().pii().name().replaceAll("\\s", ""),
            verifier.ids().pii().gender()),
        test(
            200,
            Patient.Bundle.class,
            p -> p.entry().isEmpty(),
            "Patient?given={given}",
            verifier.ids().pii().given()),
        test(
            200,
            Patient.Bundle.class,
            p -> p.entry().isEmpty(),
            "Patient?organization={organization}",
            verifier.ids().pii().organization()),
        test(200, Patient.Bundle.class, p -> p.entry().isEmpty(), "Patient/"));
  }

  @Test
  public void basic() {
    verifier.verifyAll(
        test(200, Patient.class, "Patient/{id}", verifier.ids().patient()),
        test(200, Patient.Bundle.class, "Patient?_id={id}", verifier.ids().patient()));
  }

  /**
   * The CDW database has disabled patient searching by identifier for both PROD/QA. We will test
   * this only in LOCAL mode against the sandbox db.
   */
  @Test
  public void patientIdentifierSearching() {
    assumeEnvironmentIn(Environment.LOCAL);
    verifier.verifyAll(
        test(
            200,
            Patient.Bundle.class,
            p -> !p.entry().isEmpty(),
            "Patient?identifier={id}",
            verifier.ids().patient()),
        test(
            200,
            Patient.Bundle.class,
            p -> !p.entry().isEmpty(),
            "Patient?identifier={ssn}",
            verifier.ids().pii().ssn()),
        test(
            200,
            Patient.Bundle.class,
            p -> !p.entry().isEmpty(),
            "Patient?identifier=http://va.gov/mpi|{id}",
            verifier.ids().patient()),
        test(
            200,
            Patient.Bundle.class,
            p -> !p.entry().isEmpty(),
            "Patient?identifier=http://hl7.org/fhir/sid/us-ssn|{ssn}",
            verifier.ids().pii().ssn()));
  }

  /**
   * In the PROD/QA environments, patient reading is restricted to your unique access-token. Any IDs
   * but your own are revoked with a 403 Forbidden. In environments where this restriction is
   * lifted, the result of an unknown ID should be 404 Not Found.
   */
  @Test
  public void patientMatching() {
    assumeEnvironmentNotIn(Environment.LOCAL);
    int status = (Environment.get() == Environment.LOCAL) ? 404 : 403;
    verifier.verifyAll(
        test(status, OperationOutcome.class, "Patient/{id}", verifier.ids().unknown()),
        test(status, OperationOutcome.class, "Patient?_id={id}", verifier.ids().unknown()));
  }
}
