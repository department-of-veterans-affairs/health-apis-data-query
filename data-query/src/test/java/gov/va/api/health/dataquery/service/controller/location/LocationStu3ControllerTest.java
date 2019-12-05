package gov.va.api.health.dataquery.service.controller.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.dataquery.service.controller.BundlerStu3;
import gov.va.api.health.dataquery.service.controller.ConfigurableBaseUrlPageLinksStu3;
import gov.va.api.health.dataquery.service.controller.ResourceExceptions;
import gov.va.api.health.dataquery.service.controller.WitnessProtection;
import gov.va.api.health.ids.api.IdentityService;
import gov.va.api.health.ids.api.Registration;
import gov.va.api.health.ids.api.ResourceIdentity;
import gov.va.api.health.stu3.api.bundle.BundleLink;
import gov.va.api.health.stu3.api.resources.Location;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class LocationStu3ControllerTest {
  @Autowired private LocationRepository repository;

  private IdentityService ids = mock(IdentityService.class);

  @SneakyThrows
  private static LocationEntity asEntity(DatamartLocation dm) {
    return LocationEntity.builder()
        .cdwId(dm.cdwId())
        .name(dm.name())
        .street(dm.address().line1())
        .city(dm.address().city())
        .state(dm.address().state())
        .postalCode(dm.address().postalCode())
        .payload(JacksonConfig.createMapper().writeValueAsString(dm))
        .build();
  }

  @SneakyThrows
  private static String asJson(Object o) {
    return JacksonConfig.createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
  }

  private void addMockIdentities(
      String locPubId, String locCdwId, String orgPubId, String orgCdwId) {
    ResourceIdentity locResource =
        ResourceIdentity.builder().system("CDW").resource("LOCATION").identifier(locCdwId).build();
    ResourceIdentity orgResource =
        ResourceIdentity.builder()
            .system("CDW")
            .resource("ORGANIZATION")
            .identifier(orgCdwId)
            .build();
    when(ids.lookup(locPubId)).thenReturn(List.of(locResource));
    when(ids.register(any()))
        .thenReturn(
            List.of(
                Registration.builder().uuid(locPubId).resourceIdentity(locResource).build(),
                Registration.builder().uuid(orgPubId).resourceIdentity(orgResource).build()));
  }

  @SneakyThrows
  private DatamartLocation asObject(String json) {
    return JacksonConfig.createMapper().readValue(json, DatamartLocation.class);
  }

  private LocationStu3Controller controller() {
    return new LocationStu3Controller(
        new BundlerStu3(new ConfigurableBaseUrlPageLinksStu3("http://fonzy.com", "cool")),
        repository,
        WitnessProtection.builder().identityService(ids).build());
  }

  @Test
  public void read() {
    String publicId = "abc";
    String cdwId = "123";
    String orgPubId = "def";
    String orgCdwId = "456";
    addMockIdentities(publicId, cdwId, orgPubId, orgCdwId);
    DatamartLocation dm = LocationSamplesStu3.Datamart.create().location(cdwId, orgCdwId);
    repository.save(asEntity(dm));
    Location actual = controller().read(publicId);
    assertThat(actual).isEqualTo(LocationSamplesStu3.Fhir.create().location(publicId, orgPubId));
  }

  @Test
  public void readRaw() {
    String publicId = "abc";
    String cdwId = "123";
    String orgPubId = "def";
    String orgCdwId = "456";
    addMockIdentities(publicId, cdwId, orgPubId, orgCdwId);
    HttpServletResponse servletResponse = mock(HttpServletResponse.class);
    DatamartLocation dm = LocationSamplesStu3.Datamart.create().location(cdwId, orgCdwId);
    repository.save(asEntity(dm));
    String json = controller().readRaw(publicId, servletResponse);
    assertThat(asObject(json)).isEqualTo(dm);
    verify(servletResponse).addHeader("X-VA-INCLUDES-ICN", "NONE");
  }

  @Test(expected = ResourceExceptions.NotFound.class)
  public void readRawThrowsNotFoundWhenDataIsMissing() {
    addMockIdentities("x", "x", "y", "y");
    controller().readRaw("x", mock(HttpServletResponse.class));
  }

  @Test(expected = ResourceExceptions.NotFound.class)
  public void readRawThrowsNotFoundWhenIdIsUnknown() {
    controller().readRaw("x", mock(HttpServletResponse.class));
  }

  @Test(expected = ResourceExceptions.NotFound.class)
  public void readThrowsNotFoundWhenDataIsMissing() {
    addMockIdentities("x", "x", "y", "y");
    controller().read("x");
  }

  @Test(expected = ResourceExceptions.NotFound.class)
  public void readThrowsNotFoundWhenIdIsUnknown() {
    controller().read("x");
  }

  @Test
  public void searchByAddress() {
    String street = "1901 VETERANS MEMORIAL DRIVE";
    String publicId = "abc";
    String cdwId = "123";
    String orgPubId = "def";
    String orgCdwId = "456";
    addMockIdentities(publicId, cdwId, orgPubId, orgCdwId);
    DatamartLocation dm = LocationSamplesStu3.Datamart.create().location(cdwId, orgCdwId);
    repository.save(asEntity(dm));
    Location.Bundle actual = controller().searchByAddress(street, null, null, null, 1, 1);
    assertThat(asJson(actual))
        .isEqualTo(
            asJson(
                LocationSamplesStu3.Fhir.asBundle(
                    "http://fonzy.com/cool",
                    List.of(LocationSamplesStu3.Fhir.create().location(publicId, orgPubId)),
                    LocationSamplesStu3.Fhir.link(
                        BundleLink.LinkRelation.first,
                        "http://fonzy.com/cool/Location?address=" + street,
                        1,
                        1),
                    LocationSamplesStu3.Fhir.link(
                        BundleLink.LinkRelation.self,
                        "http://fonzy.com/cool/Location?address=" + street,
                        1,
                        1),
                    LocationSamplesStu3.Fhir.link(
                        BundleLink.LinkRelation.last,
                        "http://fonzy.com/cool/Location?address=" + street,
                        1,
                        1))));
  }

  @Test
  public void searchById() {
    String publicId = "abc";
    String cdwId = "123";
    String orgPubId = "def";
    String orgCdwId = "456";
    addMockIdentities(publicId, cdwId, orgPubId, orgCdwId);
    DatamartLocation dm = LocationSamplesStu3.Datamart.create().location(cdwId, orgCdwId);
    repository.save(asEntity(dm));
    Location.Bundle actual = controller().searchById(publicId, 1, 1);
    assertThat(asJson(actual))
        .isEqualTo(
            asJson(
                LocationSamplesStu3.Fhir.asBundle(
                    "http://fonzy.com/cool",
                    List.of(LocationSamplesStu3.Fhir.create().location(publicId, orgPubId)),
                    LocationSamplesStu3.Fhir.link(
                        BundleLink.LinkRelation.first,
                        "http://fonzy.com/cool/Location?identifier=" + publicId,
                        1,
                        1),
                    LocationSamplesStu3.Fhir.link(
                        BundleLink.LinkRelation.self,
                        "http://fonzy.com/cool/Location?identifier=" + publicId,
                        1,
                        1),
                    LocationSamplesStu3.Fhir.link(
                        BundleLink.LinkRelation.last,
                        "http://fonzy.com/cool/Location?identifier=" + publicId,
                        1,
                        1))));
  }

  @Test
  public void searchByName() {
    String name = "TEM MH PSO TRS IND93EH";
    String publicId = "abc";
    String cdwId = "123";
    String orgPubId = "def";
    String orgCdwId = "456";
    addMockIdentities(publicId, cdwId, orgPubId, orgCdwId);
    DatamartLocation dm = LocationSamplesStu3.Datamart.create().location(cdwId, orgCdwId);
    repository.save(asEntity(dm));
    Location.Bundle actual = controller().searchByName(name, 1, 1);
    assertThat(asJson(actual))
        .isEqualTo(
            asJson(
                LocationSamplesStu3.Fhir.asBundle(
                    "http://fonzy.com/cool",
                    List.of(LocationSamplesStu3.Fhir.create().location(publicId, orgPubId)),
                    LocationSamplesStu3.Fhir.link(
                        BundleLink.LinkRelation.first,
                        "http://fonzy.com/cool/Location?name=" + name,
                        1,
                        1),
                    LocationSamplesStu3.Fhir.link(
                        BundleLink.LinkRelation.self,
                        "http://fonzy.com/cool/Location?name=" + name,
                        1,
                        1),
                    LocationSamplesStu3.Fhir.link(
                        BundleLink.LinkRelation.last,
                        "http://fonzy.com/cool/Location?name=" + name,
                        1,
                        1))));
  }
}
