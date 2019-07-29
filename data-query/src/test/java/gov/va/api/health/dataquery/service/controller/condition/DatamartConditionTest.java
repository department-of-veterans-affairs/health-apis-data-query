package gov.va.api.health.dataquery.service.controller.condition;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import java.time.Instant;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.Test;

public class DatamartConditionTest {

  private DatamartCondition sample() {
    return DatamartCondition.builder()
        .etlDate(Instant.parse("2011-06-27T05:40:00Z"))
        .cdwId("800274570575:D")
        .patient(
            DatamartReference.of()
                .type("Patient")
                .reference("666V666")
                .display("VETERAN,FIRNM MINAM")
                .build())
        .encounter(
            Optional.of(
                DatamartReference.of()
                    .type("Encounter")
                    .reference("800285390250")
                    .display("Outpatient Visit")
                    .build()))
        .asserter(
            Optional.of(
                DatamartReference.of()
                    .type("Practitioner")
                    .reference("1294265")
                    .display("DOCLANAM,DOCFIRNAM E")
                    .build()))
        .dateRecorded(Optional.of(Instant.parse("2011-06-27T05:40:00Z")))
        .snomed(
            Optional.of(
                DatamartCondition.SnomedCode.builder()
                    .code("70650003")
                    .display("Urinary bladder stone")
                    .build()))
        .icd(
            Optional.of(
                DatamartCondition.IcdCode.builder()
                    .code("N210")
                    .display("Calculus in bladder")
                    .version("10")
                    .build()))
        .category(DatamartCondition.Category.diagnosis)
        .clinicalStatus(DatamartCondition.ClinicalStatus.active)
        .onsetDateTime(Optional.of(Instant.parse("2011-06-27T05:40:00Z")))
        .abatementDateTime(Optional.empty())
        .build();
  }

  @Test
  @SneakyThrows
  public void unmarshalSample() {
    DatamartCondition dm =
        createMapper()
            .readValue(
                getClass().getResourceAsStream("datamart-condition.json"), DatamartCondition.class);
    assertThat(dm).isEqualTo(sample());
  }
}
