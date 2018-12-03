package gov.va.api.health.argonaut.api;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import javax.ws.rs.Path;

@OpenAPIDefinition(
  info =
      @Info(
        title = "API Management Platform | Health - Argonaut",
        version = "v1",
        description =
            "FHIR (Fast Healthcare Interoperability Resources) specification defines a set of"
                + " \"Resources\" that represent granular clinical concepts."
                + " This service is compliant with the FHIR Argonaut Data Query Implementation"
                + " Guide."
      ),
  servers = {
    @Server(url = "https://api.va.gov/services/health/v0/", description = "Development server")
  },
  externalDocs =
      @ExternalDocumentation(
        description = "Specification",
        url = "http://www.fhir.org/guides/argonaut/r2/index.html"
      )
)
@Path("api")
public interface ArgonautService
    extends AllergyIntoleranceApi,
        ConditionApi,
        DiagnosticReportApi,
        ImmunizationApi,
        MedicationOrderApi,
        MedicationApi,
        MedicationStatementApi,
        MetadataApi,
        ObservationApi,
        PatientApi,
        ProcedureApi {

  class ArgonautServiceException extends RuntimeException {
    ArgonautServiceException(String message) {
      super(message);
    }
  }

  class SearchFailed extends ArgonautServiceException {
    @SuppressWarnings("WeakerAccess")
    public SearchFailed(String id, String reason) {
      super(id + " Reason: " + reason);
    }
  }

  class UnknownResource extends ArgonautServiceException {
    @SuppressWarnings("WeakerAccess")
    public UnknownResource(String id) {
      super(id);
    }
  }
}
