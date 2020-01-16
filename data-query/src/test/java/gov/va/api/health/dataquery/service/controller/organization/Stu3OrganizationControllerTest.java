//package gov.va.api.health.dataquery.service.controller.organization;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import gov.va.api.health.autoconfig.configuration.JacksonConfig;
//import gov.va.api.health.dataquery.service.controller.ConfigurableBaseUrlPageLinks;
//import gov.va.api.health.dataquery.service.controller.Stu3Bundler;
//import gov.va.api.health.dataquery.service.controller.Stu3Validator;
//import gov.va.api.health.dataquery.service.controller.ResourceExceptions;
//import gov.va.api.health.dataquery.service.controller.WitnessProtection;
//import gov.va.api.health.ids.api.IdentityService;
//import gov.va.api.health.ids.api.Registration;
//import gov.va.api.health.ids.api.ResourceIdentity;
//import java.util.List;
//import javax.servlet.http.HttpServletResponse;
//
//import gov.va.api.health.stu3.api.bundle.BundleLink;
//import gov.va.api.health.stu3.api.resources.Organization;
//import lombok.SneakyThrows;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@DataJpaTest
//@RunWith(SpringRunner.class)
//public class Stu3OrganizationControllerTest {
//    HttpServletResponse response = mock(HttpServletResponse.class);
//
//    private IdentityService ids = mock(IdentityService.class);
//
//    @Autowired private OrganizationRepository repository;
//
//    @SneakyThrows
//    private static OrganizationEntity asEntity(DatamartOrganization dm) {
//        return OrganizationEntity.builder()
//                .cdwId(dm.cdwId())
//                .name(dm.name())
//                .city(dm.address().city())
//                .state(dm.address().state())
//                .postalCode(dm.address().postalCode())
//                .payload(JacksonConfig.createMapper().writeValueAsString(dm))
//                .build();
//    }
//
//    @SneakyThrows
//    private static String asJson(Object o) {
//        return JacksonConfig.createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
//    }
//
//    private void addMockIdentities(String orgPubId, String orgCdwId) {
//        ResourceIdentity orgResource =
//                ResourceIdentity.builder()
//                        .system("CDW")
//                        .resource("ORGANIZATION")
//                        .identifier(orgCdwId)
//                        .build();
//        when(ids.lookup(orgPubId)).thenReturn(List.of(orgResource));
//        when(ids.register(Mockito.any()))
//                .thenReturn(
//                        List.of(Registration.builder().uuid(orgPubId).resourceIdentity(orgResource).build()));
//    }
//
//    @SneakyThrows
//    private DatamartOrganization asObject(String json) {
//        return JacksonConfig.createMapper().readValue(json, DatamartOrganization.class);
//    }
//
//    private Stu3OrganizationController controller() {
//        return new Stu3OrganizationController(
//                new Stu3Bundler(new ConfigurableBaseUrlPageLinks("http://fonzy.com", "cool", "cool")),
//                repository,
//                WitnessProtection.builder().identityService(ids).build());
//    }
//
//    @Test
//    public void read() {
//        String publicId = "abc";
//        String cdwId = "123";
//        addMockIdentities(publicId, cdwId);
//        DatamartOrganization dm = Dstu2OrganizationSamples.Datamart.create().organization();
//        repository.save(asEntity(dm));
//        Organization actual = controller().read("1234");
//        assertThat(actual).isEqualTo(Stu3OrganizationSamples.Stu3.create().organization("1234"));
//    }
//
//    @Test
//    public void readRaw() {
//        String publicId = "abc";
//        String cdwId = "123";
//        addMockIdentities(publicId, cdwId);
//        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
//        DatamartOrganization dm = Dstu2OrganizationSamples.Datamart.create().organization(cdwId);
//        repository.save(asEntity(dm));
//        String json = controller().readRaw(publicId, servletResponse);
//        assertThat(asObject(json)).isEqualTo(dm);
//        verify(servletResponse).addHeader("X-VA-INCLUDES-ICN", "NONE");
//    }
//
//    @Test(expected = ResourceExceptions.NotFound.class)
//    public void readRawThrowsNotFoundWhenDataIsMissing() {
//        addMockIdentities("x", "y");
//        controller().readRaw("x", mock(HttpServletResponse.class));
//    }
//
//    @Test(expected = ResourceExceptions.NotFound.class)
//    public void readRawThrowsNotFoundWhenIdIsUnknown() {
//        controller().readRaw("x", mock(HttpServletResponse.class));
//    }
//
//    @Test(expected = ResourceExceptions.NotFound.class)
//    public void readThrowsNotFoundWhenDataIsMissing() {
//        addMockIdentities("x", "y");
//        controller().read("x");
//    }
//
//    @Test(expected = ResourceExceptions.NotFound.class)
//    public void readThrowsNotFoundWhenIdIsUnknown() {
//        controller().read("x");
//    }
//
//    @Test
//    public void searchById() {
//        String publicId = "abc";
//        String cdwId = "123";
//        addMockIdentities(publicId, cdwId);
//        DatamartOrganization dm = Dstu2OrganizationSamples.Datamart.create().organization(cdwId);
//        repository.save(asEntity(dm));
//        Organization.Bundle actual = controller().searchById(publicId, 1, 1);
//        assertThat(asJson(actual))
//                .isEqualTo(
//                        asJson(
//                                Dstu2OrganizationSamples.Stu3.asBundle(
//                                        "http://fonzy.com/cool",
//                                        List.of(Dstu2OrganizationSamples.Stu3.create().organization(publicId)),
//                                        Dstu2OrganizationSamples.Stu3.link(
//                                                BundleLink.LinkRelation.first,
//                                                "http://fonzy.com/cool/Organization?identifier=" + publicId,
//                                                1,
//                                                1),
//                                        Dstu2OrganizationSamples.Stu3.link(
//                                                BundleLink.LinkRelation.self,
//                                                "http://fonzy.com/cool/Organization?identifier=" + publicId,
//                                                1,
//                                                1),
//                                        Dstu2OrganizationSamples.Stu3.link(
//                                                BundleLink.LinkRelation.last,
//                                                "http://fonzy.com/cool/Organization?identifier=" + publicId,
//                                                1,
//                                                1))));
//    }
//
//    @Test
//    public void searchByIdentifier() {
//        String publicId = "abc";
//        String cdwId = "123";
//        addMockIdentities(publicId, cdwId);
//        DatamartOrganization dm = Dstu2OrganizationSamples.Datamart.create().organization(cdwId);
//        repository.save(asEntity(dm));
//        Organization.Bundle actual = controller().searchByIdentifier(publicId, 1, 1);
//        assertThat(asJson(actual))
//                .isEqualTo(
//                        asJson(
//                                Dstu2OrganizationSamples.Stu3.asBundle(
//                                        "http://fonzy.com/cool",
//                                        List.of(Dstu2OrganizationSamples.Stu3.create().organization(publicId)),
//                                        Dstu2OrganizationSamples.Stu3.link(
//                                                BundleLink.LinkRelation.first,
//                                                "http://fonzy.com/cool/Organization?identifier=" + publicId,
//                                                1,
//                                                1),
//                                        Dstu2OrganizationSamples.Stu3.link(
//                                                BundleLink.LinkRelation.self,
//                                                "http://fonzy.com/cool/Organization?identifier=" + publicId,
//                                                1,
//                                                1),
//                                        Dstu2OrganizationSamples.Stu3.link(
//                                                BundleLink.LinkRelation.last,
//                                                "http://fonzy.com/cool/Organization?identifier=" + publicId,
//                                                1,
//                                                1))));
//    }
//
//    @Test
//    public void validate() {
//        DatamartOrganization dm = Dstu2OrganizationSamples.Datamart.create().organization();
//        Organization organization = Dstu2OrganizationSamples.Stu3.create().organization();
//        assertThat(
//                controller()
//                        .validate(
//                                Dstu2OrganizationSamples.Stu3.asBundle(
//                                        "http://fonzy.com/cool",
//                                        List.of(organization),
//                                        Dstu2OrganizationSamples.Stu3.link(
//                                                BundleLink.LinkRelation.first,
//                                                "http://fonzy.com/cool/AllergyIntolerance?identifier=1",
//                                                1,
//                                                1),
//                                        Dstu2OrganizationSamples.Stu3.link(
//                                                BundleLink.LinkRelation.self,
//                                                "http://fonzy.com/cool/AllergyIntolerance?identifier=1",
//                                                1,
//                                                1),
//                                        Dstu2OrganizationSamples.Stu3.link(
//                                                BundleLink.LinkRelation.last,
//                                                "http://fonzy.com/cool/AllergyIntolerance?identifier=1",
//                                                1,
//                                                1))))
//                .isEqualTo(Stu3Validator.ok());
//    }
//}