package gov.va.api.health.dataquery.service.controller.practitioner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.dataquery.service.controller.ConfigurableBaseUrlPageLinks;
import gov.va.api.health.dataquery.service.controller.Dstu2Bundler;
import gov.va.api.health.dataquery.service.controller.ResourceExceptions;
import gov.va.api.health.dataquery.service.controller.WitnessProtection;
import gov.va.api.health.dstu2.api.bundle.BundleLink.LinkRelation;
import gov.va.api.health.dstu2.api.resources.Practitioner;
import gov.va.api.health.ids.api.IdentityService;
import gov.va.api.health.ids.api.Registration;
import gov.va.api.health.ids.api.ResourceIdentity;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class Dstu2PractitionerControllerTest {
  HttpServletResponse response = mock(HttpServletResponse.class);

  IdentityService ids = mock(IdentityService.class);

  @Autowired PractitionerRepository repository;

  static Registration idReg(String type, String pubId, String cdwId) {
    return Registration.builder()
        .uuid(pubId)
        .resourceIdentities(
            List.of(
                ResourceIdentity.builder().system("CDW").resource(type).identifier(cdwId).build()))
        .build();
  }

  @SneakyThrows
  static String json(Object o) {
    return JacksonConfig.createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
  }

  @SneakyThrows
  static DatamartPractitioner toObject(String json) {
    return JacksonConfig.createMapper().readValue(json, DatamartPractitioner.class);
  }

  Dstu2PractitionerController _controller() {
    return new Dstu2PractitionerController(
        new Dstu2Bundler(
            new ConfigurableBaseUrlPageLinks("http://fonzy.com", "cool", "cool", "cool")),
        repository,
        WitnessProtection.builder().identityService(ids).build());
  }

  void _registerMockIdentities(Registration... regs) {
    for (Registration reg : regs) {
      when(ids.lookup(reg.uuid())).thenReturn(reg.resourceIdentities());
    }
    when(ids.register(any())).thenReturn(ImmutableList.copyOf(regs));
  }

  @Test
  void read() {
    String publicId = "I2-abc";
    String cdwId = "111:S";
    String orgPubId = "I2-def";
    String orgCdwId = "222:I";
    String locPubId = "I2-ghi";
    String locCdwId = "333:L";
    repository.save(PractitionerSamples.Datamart.create().entity(cdwId, orgCdwId, locCdwId));
    _registerMockIdentities(
        idReg("PRACTITIONER", publicId, cdwId),
        idReg("ORGANIZATION", orgPubId, orgCdwId),
        idReg("LOCATION", locPubId, locCdwId));
    Practitioner actual = _controller().read(cdwId);
    assertThat(actual)
        .isEqualTo(PractitionerSamples.Dstu2.create().practitioner(publicId, orgPubId, locPubId));
  }

  @Test
  void readRaw() {
    String publicId = "I2-abc";
    String cdwId = "123:S";
    String orgPubId = "I2-def";
    String orgCdwId = "456:I";
    String locPubId = "I2-ghi";
    String locCdwId = "789:L";
    _registerMockIdentities(
        idReg("PRACTITIONER", publicId, cdwId),
        idReg("LOCATION", orgPubId, orgCdwId),
        idReg("ORGANIZATION", locPubId, locCdwId));
    HttpServletResponse servletResponse = mock(HttpServletResponse.class);
    DatamartPractitioner dm =
        PractitionerSamples.Datamart.create().practitioner(cdwId, orgCdwId, locCdwId);
    repository.save(PractitionerSamples.Datamart.create().entity(cdwId, orgCdwId, locCdwId));
    String json = _controller().readRaw(publicId, servletResponse);
    assertThat(toObject(json)).isEqualTo(dm);
    verify(servletResponse).addHeader("X-VA-INCLUDES-ICN", "NONE");
  }

  @Test
  void readRawThrowsNotFoundWhenDataIsMissing() {
    _registerMockIdentities(
        idReg("PRACTITIONER", "x", "x"),
        idReg("LOCATION", "x", "x"),
        idReg("ORGANIZATION", "x", "x"));
    assertThrows(ResourceExceptions.NotFound.class, () -> _controller().readRaw("x", response));
  }

  @Test
  void readRawThrowsNotFoundWhenIdIsUnknown() {
    assertThrows(ResourceExceptions.NotFound.class, () -> _controller().readRaw("x", response));
  }

  @Test
  void readRaw_migrate() {
    String publicIdNoSuffix = "I2-abc";
    String cdwIdNoSuffix = "123";
    String publicIdSuffix = "I2-abcS";
    String cdwIdSuffix = "123:S";
    _registerMockIdentities(
        idReg("PRACTITIONER", publicIdNoSuffix, cdwIdNoSuffix),
        idReg("PRACTITIONER", publicIdSuffix, cdwIdSuffix),
        idReg("ORGANIZATION", "I2-def", "456:I"),
        idReg("LOCATION", "I2-ghi", "789:L"));
    HttpServletResponse servletResponse = mock(HttpServletResponse.class);
    DatamartPractitioner dm =
        PractitionerSamples.Datamart.create().practitioner(cdwIdSuffix, "456:I", "789:L");
    repository.save(PractitionerSamples.Datamart.create().entity(cdwIdSuffix, "456:I", "789:L"));
    String json = _controller().readRaw(publicIdNoSuffix, servletResponse);
    assertThat(toObject(json)).isEqualTo(dm);
    verify(servletResponse).addHeader("X-VA-INCLUDES-ICN", "NONE");
  }

  @Test
  void readThrowsNotFoundWhenDataIsMissing() {
    _registerMockIdentities(
        idReg("PRACTITIONER", "x", "x"),
        idReg("LOCATION", "x", "x"),
        idReg("ORGANIZATION", "x", "x"));
    assertThrows(ResourceExceptions.NotFound.class, () -> _controller().read("x"));
  }

  @Test
  void readThrowsNotFoundWhenIdIsUnknown() {
    assertThrows(ResourceExceptions.NotFound.class, () -> _controller().read("x"));
  }

  @Test
  void read_migrate() {
    String publicIdNoSuffix = "I2-abc";
    String cdwIdNoSuffix = "123";
    String publicIdSuffix = "I2-abcS";
    String cdwIdSuffix = "123:S";
    String orgPubId = "I2-def";
    String orgCdwId = "456:I";
    String locPubId = "I2-ghi";
    String locCdwId = "789:L";
    _registerMockIdentities(
        idReg("PRACTITIONER", publicIdNoSuffix, cdwIdNoSuffix),
        idReg("PRACTITIONER", publicIdSuffix, cdwIdSuffix),
        idReg("ORGANIZATION", orgPubId, orgCdwId),
        idReg("LOCATION", locPubId, locCdwId));
    repository.save(PractitionerSamples.Datamart.create().entity(cdwIdSuffix, orgCdwId, locCdwId));
    Practitioner actual = _controller().read(publicIdNoSuffix);
    assertThat(actual)
        .isEqualTo(
            PractitionerSamples.Dstu2.create().practitioner(publicIdSuffix, orgPubId, locPubId));
  }

  @Test
  void searchById() {
    String publicId = "I2-abc";
    String cdwId = "123:S";
    String orgPubId = "I2-def";
    String orgCdwId = "456:I";
    String locPubId = "I2-ghi";
    String locCdwId = "789:L";
    _registerMockIdentities(
        idReg("PRACTITIONER", publicId, cdwId),
        idReg("ORGANIZATION", orgPubId, orgCdwId),
        idReg("LOCATION", locPubId, locCdwId));
    repository.save(PractitionerSamples.Datamart.create().entity(cdwId, orgCdwId, locCdwId));
    Practitioner.Bundle actual = _controller().searchById(publicId, 1, 1);
    assertThat(json(actual))
        .isEqualTo(
            json(
                PractitionerSamples.Dstu2.asBundle(
                    "http://fonzy.com/cool",
                    List.of(
                        PractitionerSamples.Dstu2.create()
                            .practitioner(publicId, orgPubId, locPubId)),
                    PractitionerSamples.Dstu2.link(
                        LinkRelation.first,
                        "http://fonzy.com/cool/Practitioner?identifier=I2-abc",
                        1,
                        1),
                    PractitionerSamples.Dstu2.link(
                        LinkRelation.self,
                        "http://fonzy.com/cool/Practitioner?identifier=I2-abc",
                        1,
                        1),
                    PractitionerSamples.Dstu2.link(
                        LinkRelation.last,
                        "http://fonzy.com/cool/Practitioner?identifier=I2-abc",
                        1,
                        1))));
  }

  @Test
  void searchByIdentifier() {
    String publicId = "I2-abc";
    String cdwId = "123:S";
    String orgPubId = "I2-def";
    String orgCdwId = "456:I";
    String locPubId = "I2-ghi";
    String locCdwId = "789:L";
    _registerMockIdentities(
        idReg("PRACTITIONER", publicId, cdwId),
        idReg("LOCATION", locPubId, locCdwId),
        idReg("ORGANIZATION", orgPubId, orgCdwId));
    repository.save(PractitionerSamples.Datamart.create().entity(cdwId, orgCdwId, locCdwId));
    Practitioner.Bundle actual = _controller().searchByIdentifier(publicId, 1, 1);
    assertThat(json(actual))
        .isEqualTo(
            json(
                PractitionerSamples.Dstu2.asBundle(
                    "http://fonzy.com/cool",
                    List.of(
                        PractitionerSamples.Dstu2.create()
                            .practitioner(publicId, orgPubId, locPubId)),
                    PractitionerSamples.Dstu2.link(
                        LinkRelation.first,
                        "http://fonzy.com/cool/Practitioner?identifier=I2-abc",
                        1,
                        1),
                    PractitionerSamples.Dstu2.link(
                        LinkRelation.self,
                        "http://fonzy.com/cool/Practitioner?identifier=I2-abc",
                        1,
                        1),
                    PractitionerSamples.Dstu2.link(
                        LinkRelation.last,
                        "http://fonzy.com/cool/Practitioner?identifier=I2-abc",
                        1,
                        1))));
  }
}
