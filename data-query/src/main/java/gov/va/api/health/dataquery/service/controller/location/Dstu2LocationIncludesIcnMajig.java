package gov.va.api.health.dataquery.service.controller.location;

import gov.va.api.health.dstu2.api.resources.Location;
import gov.va.api.health.dataquery.service.controller.AbstractIncludesIcnMajig;
import gov.va.api.health.dataquery.service.controller.Dstu2IncludesIcnMajig;

import java.util.stream.Stream;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class Dstu2LocationIncludesIcnMajig extends AbstractIncludesIcnMajig {
  public Dstu2LocationIncludesIcnMajig() {
    super(
        new Dstu2IncludesIcnMajig<>(
            Location.class, Location.Bundle.class, (body) -> Stream.empty()));
  }
}
