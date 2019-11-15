package gov.va.api.health.dataquery.service.controller.organization;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import gov.va.api.health.dataquery.service.controller.datamart.DatamartCoding;
import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import gov.va.api.health.dataquery.service.controller.datamart.HasReplaceableId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatamartOrganization implements HasReplaceableId {
  @Builder.Default private String objectType = "Organization";

  @Builder.Default private String objectVersion = "1";

  private String cdwId;

  private Optional<String> stationIdentifier;
  /** Lazy initialization. */
  public Optional<String> stationIdentifier() {
    if (stationIdentifier == null) {
      stationIdentifier = Optional.empty();
    }
    return stationIdentifier;
  }

  private Optional<String> npi;
  /** Lazy initialization. */
  public Optional<String> npi() {
    if (npi == null) {
      npi = Optional.empty();
    }
    return npi;
  }

  private Optional<String> providerId;
  /** Lazy initialization. */
  public Optional<String> providerId() {
    if (providerId == null) {
      providerId = Optional.empty();
    }
    return providerId;
  }

  private Optional<String> ediId;
  /** Lazy initialization. */
  public Optional<String> ediId() {
    if (ediId == null) {
      ediId = Optional.empty();
    }
    return ediId;
  }

  private Optional<String> agencyId;
  /** Lazy initialization. */
  public Optional<String> agencyId() {
    if (agencyId == null) {
      agencyId = Optional.empty();
    }
    return agencyId;
  }

  private Boolean active;

  private Optional<DatamartCoding> type;
  /** Lazy initialization. */
  public Optional<DatamartCoding> type() {
    if (type == null) {
      type = Optional.empty();
    }
    return type;
  }

  private String name;

  private List<Telecom> telecom;
  /** Lazy initialization. */
  public List<Telecom> telecom() {
    if (telecom == null) {
      telecom = new ArrayList<>();
    }
    return telecom;
  }

  private Address address;

  private Optional<DatamartReference> partOf;
  /** Lazy initialization. */
  public Optional<DatamartReference> partOf() {
    if (partOf == null) {
      partOf = Optional.empty();
    }
    return partOf;
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class Address {
    private String line1;

    private String line2;

    private String city;

    private String state;

    private String postalCode;
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class Telecom {
    private System system;

    private String value;

    public enum System {
      phone,
      fax
    }
  }
}
