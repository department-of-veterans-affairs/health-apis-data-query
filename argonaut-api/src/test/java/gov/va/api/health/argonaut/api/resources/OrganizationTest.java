package gov.va.api.health.argonaut.api.resources;

import static gov.va.api.health.argonaut.api.RoundTrip.assertRoundTrip;

import gov.va.api.health.argonaut.api.bundle.AbstractBundle.BundleType;
import gov.va.api.health.argonaut.api.bundle.BundleLink;
import gov.va.api.health.argonaut.api.bundle.BundleLink.LinkRelation;
import gov.va.api.health.argonaut.api.resources.Organization.Bundle;
import gov.va.api.health.argonaut.api.resources.Organization.Entry;
import gov.va.api.health.argonaut.api.samples.SampleOrganizations;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class OrganizationTest {
  private final SampleOrganizations data = SampleOrganizations.get();

  @Test
  public void bundlerCanBuildOrganizationBundles() {
    Entry entry =
        Entry.builder()
            .extension(Collections.singletonList(data.extension()))
            .fullUrl("http://organization.com")
            .id("123")
            .link(
                Collections.singletonList(
                    BundleLink.builder()
                        .relation(LinkRelation.self)
                        .url(("http://organization.com/1"))
                        .build()))
            .resource(data.organization())
            .search(data.search())
            .request(data.request())
            .response(data.response())
            .build();

    Bundle bundle =
        Bundle.builder()
            .entry(Collections.singletonList(entry))
            .link(
                Collections.singletonList(
                    BundleLink.builder()
                        .relation(LinkRelation.self)
                        .url(("http://organization.com/2"))
                        .build()))
            .type(BundleType.searchset)
            .build();

    assertRoundTrip(bundle);
  }

  @Test
  public void organization() {
    assertRoundTrip(data.organization());
  }
}
