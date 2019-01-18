package gov.va.api.health.argonaut.api;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import javax.ws.rs.Path;

@OpenAPIDefinition(
  info =
      @Info(
        title = "Argonaut",
        version = "v1",
        description =
            "FHIR (Fast Healthcare Interoperability Resources) specification defines a set of"
                + " \"Resources\" that represent granular clinical concepts."
                + " This service is compliant with the FHIR Argonaut Data Query Implementation"
                + " Guide."
      ),
  servers = {
    @Server(
      url = "https://dev-api.va.gov/services/argonaut/v0/",
      description = "Development server"
    )
  },
  externalDocs =
      @ExternalDocumentation(
        description = "Argonaut Data Query Implementation Guide",
        url = "http://www.fhir.org/guides/argonaut/r2/index.html"
      )
)
@Path("/")
public interface ArgonautService
    extends AllergyIntoleranceApi,
        ConditionApi,
        DiagnosticReportApi,
        ImmunizationApi,
        MedicationOrderApi,
        MedicationApi,
        MedicationDispenseApi,
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
