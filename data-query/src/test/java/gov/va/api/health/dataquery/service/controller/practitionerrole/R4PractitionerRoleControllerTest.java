package gov.va.api.health.dataquery.service.controller.practitionerrole;

import static gov.va.api.health.dataquery.service.controller.MockRequests.paging;
import static gov.va.api.health.dataquery.service.controller.MockRequests.requestFromUri;
import static gov.va.api.health.dataquery.service.controller.practitionerrole.PractitionerRoleSamples.id;
import static gov.va.api.health.dataquery.service.controller.practitionerrole.PractitionerRoleSamples.registration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.dataquery.service.config.LinkProperties;
import gov.va.api.health.dataquery.service.controller.ResourceExceptions.NotImplemented;
import gov.va.api.health.dataquery.service.controller.WitnessProtection;
import gov.va.api.health.dataquery.service.controller.practitioner.PractitionerEntity;
import gov.va.api.health.dataquery.service.controller.practitioner.PractitionerRepository;
import gov.va.api.health.dataquery.service.controller.practitionerrole.PractitionerRoleSamples.Datamart;
import gov.va.api.health.dataquery.service.controller.practitionerrole.PractitionerRoleSamples.R4;
import gov.va.api.health.ids.api.IdentityService;
import gov.va.api.health.r4.api.bundle.BundleLink.LinkRelation;
import gov.va.api.health.r4.api.resources.PractitionerRole;
import gov.va.api.lighthouse.vulcan.InvalidRequest;
import gov.va.api.lighthouse.vulcan.VulcanResult;
import gov.va.api.lighthouse.vulcan.mappings.TokenParameter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class R4PractitionerRoleControllerTest {
  @Mock IdentityService ids;

  @Mock PractitionerRepository repository;

  R4PractitionerRoleController controller() {
    return new R4PractitionerRoleController(
        WitnessProtection.builder().identityService(ids).build(),
        LinkProperties.builder()
            .publicUrl("http://fonzy.com")
            .publicR4BasePath("r4")
            .publicStu3BasePath("stu3")
            .publicDstu2BasePath("dstu2")
            .maxPageSize(20)
            .defaultPageSize(15)
            .build(),
        repository);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "?practitioner.identifier=123&practitioner.name=Foo",
        "?identifier=123&practitioner.identifier=123",
        "?_id=123&identifier=123",
        "?identifier=123&practitioner.name=Foo"
      })
  @SneakyThrows
  public void invalidRequests(String query) {
    var r = requestFromUri("http://fonzy.com/r4/PractitionerRole" + query);
    assertThatExceptionOfType(InvalidRequest.class).isThrownBy(() -> controller().search(r));
  }

  @Test
  void read() {
    when(ids.register(any())).thenReturn(List.of(registration("pr1", "ppr1")));
    when(ids.lookup("ppr1")).thenReturn(List.of(id("pr1")));
    PractitionerEntity entity = Datamart.create().entity("pr1", "loc1", "org1");
    when(repository.findById("pr1")).thenReturn(Optional.of(entity));
    assertThat(controller().read("ppr1"))
        .isEqualTo(PractitionerRoleSamples.R4.create().practitionerRole("ppr1", "org1", "loc1"));
  }

  @Test
  void readRaw() {
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(ids.lookup("ppr1")).thenReturn(List.of(id("pr1")));
    PractitionerEntity entity =
        PractitionerEntity.builder().npi("12345").payload("payload!").build();
    when(repository.findById("pr1")).thenReturn(Optional.of(entity));
    assertThat(controller().readRaw("ppr1", response)).isEqualTo("payload!");
  }

  @Test
  public void toBundle() {
    when(ids.register(any()))
        .thenReturn(
            List.of(
                registration("pr1", "ppr1"),
                registration("pr2", "ppr2"),
                registration("pr3", "ppr3")));
    var bundler = controller().toBundle();
    Datamart datamart = Datamart.create();
    var vr =
        VulcanResult.<PractitionerEntity>builder()
            .paging(
                paging(
                    "http://fonzy.com/r4/PractitionerRole?practitioner.identifier=pr1&page=%d&_count=%d",
                    1, 4, 5, 6, 9, 15))
            .entities(
                Stream.of(
                    datamart.entity("pr1", "loc1", "org1"),
                    datamart.entity("pr2", "loc2", "org2"),
                    datamart.entity("pr3", "loc3", "org3")))
            .build();
    R4 r4 = R4.create();
    PractitionerRole.Bundle expected =
        R4.asBundle(
            "http://fonzy.com/r4",
            List.of(
                r4.practitionerRole("ppr1", "org1", "loc1"),
                r4.practitionerRole("ppr2", "org2", "loc2"),
                r4.practitionerRole("ppr3", "org3", "loc3")),
            999,
            R4.link(
                LinkRelation.first,
                "http://fonzy.com/r4/PractitionerRole?practitioner.identifier=pr1",
                1,
                15),
            R4.link(
                LinkRelation.prev,
                "http://fonzy.com/r4/PractitionerRole?practitioner.identifier=pr1",
                4,
                15),
            R4.link(
                LinkRelation.self,
                "http://fonzy.com/r4/PractitionerRole?practitioner.identifier=pr1",
                5,
                15),
            R4.link(
                LinkRelation.next,
                "http://fonzy.com/r4/PractitionerRole?practitioner.identifier=pr1",
                6,
                15),
            R4.link(
                LinkRelation.last,
                "http://fonzy.com/r4/PractitionerRole?practitioner.identifier=pr1",
                9,
                15));
    var applied = bundler.apply(vr);
    assertThat(applied).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({
    "12345,false",
    "|12345,false",
    "http://example.com/bad/system|12345,false",
    "http://example.com/bad/system|,false",
    "http://hl7.org/fhir/sid/us-npi|12345,true",
    "http://hl7.org/fhir/sid/us-npi|,false",
    "http://hl7.org/fhir/sid/us-npi,false"
  })
  void tokenPractitionerIdentifierIsSupported(String parameterValue, boolean expectedSupport) {
    var token = TokenParameter.parse("practitioner.identifier", parameterValue);
    assertThat(controller().tokenPractitionerIdentifierIsSupported(token))
        .isEqualTo(expectedSupport);
  }

  @ParameterizedTest
  @CsvSource({"12345,12345", "http://hl7.org/fhir/sid/us-npi|12345,12345"})
  void tokenPractitionerIdentifierValue(String parameterValue, String expected) {
    var token = TokenParameter.parse("practitioner.identifier", parameterValue);
    assertThat(controller().tokenPractitionerIdentifierValue(token)).contains(expected);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "?specialty=http://nucc.org/provider-taxonomy|208D0000X",
        "?practitioner.identifier=1932127842&specialty=208D0000X"
      })
  @SneakyThrows
  void unimplementedSpecialty(String query) {
    var r = requestFromUri("http://fonzy.com/r4/PractitionerRole" + query);
    assertThatExceptionOfType(NotImplemented.class).isThrownBy(() -> controller().search(r));
  }
}