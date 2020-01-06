package gov.va.api.health.dataquery.service.controller.medicationstatement;

import gov.va.api.health.argonaut.api.resources.MedicationStatement;
import gov.va.api.health.dataquery.service.controller.Dstu2Transformers;
import gov.va.api.health.dataquery.service.controller.IncludesIcnMajig;
import gov.va.api.health.dstu2.api.bundle.AbstractEntry;
import java.util.stream.Stream;
import lombok.experimental.Delegate;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Intercept all RequestMapping payloads of Type MedicationStatement.class or Bundle.class. Extract
 * ICN(s) from these payloads with the provided function. This will lead to populating the
 * X-VA-INCLUDES-ICN header.
 */
@ControllerAdvice
public class Dstu2MedicationStatementIncludesIcnMajig {
  @Delegate
  private final IncludesIcnMajig<MedicationStatement, MedicationStatement.Bundle> delegate =
      IncludesIcnMajig.<MedicationStatement, MedicationStatement.Bundle>builder()
          .type(MedicationStatement.class)
          .bundleType(MedicationStatement.Bundle.class)
          .extractResources(bundle -> bundle.entry().stream().map(AbstractEntry::resource))
          .extractIcns(body -> Stream.ofNullable(Dstu2Transformers.asReferenceId(body.patient())))
          .build();
}
