package gov.va.api.health.dataquery.patientregistration;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import({FugaziApplication.class, JacksonConfig.class})
@TestPropertySource(
    properties = {
      "ssl.enable-client=false",
      "dynamo-patient-registrar.endpoint=http://localhost:8000",
      "dynamo-patient-registrar.region=us-gov-west-1",
      "dynamo-patient-registrar.table=patient-registration-local",
      "dynamo-patient-registrar.enabled=false"
    })
@Slf4j
public class PatientRegistrationFilterSpringTest {
  @Autowired TestRestTemplate rest;

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/fugazi/Patient/123",
        "/fugazi/Patient?_id=123",
        "/fugazi/Patient?identifier=123"
      })
  void filterApplied(String url) {
    try (var db = LocalDynamoDb.startDefault()) {
      ResponseEntity<String> response = rest.getForEntity(url, String.class);
      assertThat(response.getHeaders().get(PatientRegistrationFilter.REGISTRATION_HEADER))
          .isNotNull();
    }
  }
}
