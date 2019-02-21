package gov.va.api.health.argonaut.service.controller.wellknown;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.argonaut.api.resources.WellKnown;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import lombok.SneakyThrows;
import org.junit.Test;

public class WellKnownControllerTest {
  @SneakyThrows
  private String pretty(WellKnown wellKnown) {
    return JacksonConfig.createMapper()
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(wellKnown);
  }

  private WellKnownProperties properties() {
    return WellKnownProperties.builder()
        .security(
            WellKnownProperties.SecurityProperties.builder()
                .tokenEndpoint("https://argonaut.lighthouse.va.gov/token")
                .authorizeEndpoint("https://argonaut.lighthouse.va.gov/authorize")
                .build())
        .capabilities("context-standalone-patient launch-ehr permission-offline permission-patient")
        .responseTypeSupported("code refresh-token")
        .scopesSupported("patient/DiagnosticReport.read patient/Patient.read offline_access")
        .build();
  }

  @Test
  @SneakyThrows
  public void read() {
    WellKnownProperties properties = properties();
    WellKnownController controller = new WellKnownController(properties);
    WellKnown old =
        JacksonConfig.createMapper()
            .readValue(getClass().getResourceAsStream("/well-known.json"), WellKnown.class);
    try {
      assertThat(pretty(controller.read())).isEqualTo(pretty(old));
    } catch (AssertionError e) {
      System.out.println(e.getMessage());
      throw e;
    }
  }
}
