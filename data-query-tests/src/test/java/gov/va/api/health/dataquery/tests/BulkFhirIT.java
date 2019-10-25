package gov.va.api.health.dataquery.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import gov.va.api.health.dataquery.service.controller.BulkFhirCount;
import gov.va.api.health.dataquery.tests.categories.LabDataQueryPatient;
import gov.va.api.health.dataquery.tests.categories.ProdDataQueryPatient;
import gov.va.api.health.sentinel.categories.Local;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Slf4j
public class BulkFhirIT {

  private final ResourceVerifier verifier = ResourceVerifier.get();

  private final RequestSpecification bulk =
      RestAssured.given()
          .spec(verifier.dataQuery().service().requestSpecification())
          .headers(ImmutableMap.of("bulk", System.getProperty("bulk-token", "some-token")));

  private final String apiPath() {
    return verifier.dataQuery().service().apiPath();
  }

  @Test
  @Category({Local.class, LabDataQueryPatient.class, ProdDataQueryPatient.class})
  @SneakyThrows
  public void bulkPatientCount() {
    verifyBulkFhirCount("Patient");
  }

  /**
   * Ensures the response is valid and the count is non-trivial. The count can change between
   * environments or if records were added. Little is gained from checking for a larger number of
   * records.
   */
  public void verifyBulkFhirCount(String resource) {
    String path = apiPath() + "internal/bulk/" + resource + "/count";
    log.info("Verify count for {} is valid", path);
    Response response = bulk.get(path);
    assertThat(response.getStatusCode()).isEqualTo(200);
    BulkFhirCount bulkFhirCount = response.as(BulkFhirCount.class);
    assertThat(bulkFhirCount.count()).isGreaterThan(3);
  }
}
