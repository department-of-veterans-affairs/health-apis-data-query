package gov.va.api.health.dataquery.service.controller.medicationdispense;

import gov.va.api.health.dataquery.service.controller.CountParameter;
import gov.va.api.health.dataquery.service.controller.Dstu2Validator;
import gov.va.api.health.dataquery.service.controller.ResourceExceptions;
import gov.va.api.health.dstu2.api.resources.MedicationDispense;
import gov.va.api.health.dstu2.api.resources.OperationOutcome;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("WeakerAccess")
@RestController
@RequestMapping(
  value = {"/dstu2/MedicationDispense"},
  produces = {"application/json", "application/json+fhir", "application/fhir+json"}
)
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class Dstu2MedicationDispenseController {

  /** Reading by id. */
  @GetMapping(value = {"/{publicId}"})
  public MedicationDispense read(@PathVariable("publicId") String publicId) {
    throw new ResourceExceptions.NotImplemented("not-implemented");
  }

  /** Searching by _id. */
  @GetMapping(params = {"_id"})
  public MedicationDispense.Bundle searchById(
      @RequestParam("_id") String id,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    throw new ResourceExceptions.NotImplemented("not-implemented");
  }

  /** Searching by identifier. */
  @GetMapping(params = {"identifier"})
  public MedicationDispense.Bundle searchByIdentifier(
      @RequestParam("identifier") String id,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    throw new ResourceExceptions.NotImplemented("not-implemented");
  }

  /** Searching by patient. */
  @GetMapping(params = {"patient"})
  public MedicationDispense.Bundle searchByPatient(
      @RequestParam("patient") String patient,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    throw new ResourceExceptions.NotImplemented("not-implemented");
  }

  /** Searching by patient and status. */
  @GetMapping(params = {"patient", "status"})
  public MedicationDispense.Bundle searchByPatientAndStatus(
      @RequestParam("patient") String patient,
      @RequestParam("status") String status,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    throw new ResourceExceptions.NotImplemented("not-implemented");
  }

  /** Searching by patient and type. */
  @GetMapping(params = {"patient", "type"})
  public MedicationDispense.Bundle searchByPatientAndType(
      @RequestParam("patient") String patient,
      @RequestParam("type") String type,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    throw new ResourceExceptions.NotImplemented("not-implemented");
  }

  /** Validation endpoint. */
  @PostMapping(
    value = "/$validate",
    consumes = {"application/json", "application/json+fhir", "application/fhir+json"}
  )
  public OperationOutcome validate(@RequestBody MedicationDispense.Bundle bundle) {
    return Dstu2Validator.create().validate(bundle);
  }
}