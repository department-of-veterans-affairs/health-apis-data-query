package gov.va.api.health.dataquery.service.controller.location;

import gov.va.api.health.dataquery.service.controller.FacilityId;
import gov.va.api.lighthouse.datamart.DatamartReference;
import gov.va.api.lighthouse.datamart.HasReplaceableId;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datamart location JSON model. */
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatamartLocation implements HasReplaceableId {
  @Builder.Default private String objectType = "Location";

  @Builder.Default private String objectVersion = "1";

  private String cdwId;

  private Status status;

  private String name;

  private Optional<String> description;

  private Optional<String> type;

  private String telecom;

  private Address address;

  private Optional<String> physicalType;

  private DatamartReference managingOrganization;

  private Optional<FacilityId> facilityId;

  private Optional<String> locationIen;

  /** Lazy initialization. */
  public Optional<String> description() {
    if (description == null) {
      description = Optional.empty();
    }
    return description;
  }

  /** Lazy initialization. */
  public Optional<FacilityId> facilityId() {
    if (facilityId == null) {
      facilityId = Optional.empty();
    }
    return facilityId;
  }

  /** Lazy initialization. */
  public Optional<String> locationIen() {
    if (locationIen == null) {
      locationIen = Optional.empty();
    }
    return locationIen;
  }

  /** Lazy initialization. */
  public Optional<String> physicalType() {
    if (physicalType == null) {
      physicalType = Optional.empty();
    }
    return physicalType;
  }

  /** Lazy initialization. */
  public Optional<String> type() {
    if (type == null) {
      type = Optional.empty();
    }
    return type;
  }

  /** status. */
  public enum Status {
    active,
    inactive
  }

  /** Address. */
  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Address {
    private String line1;

    private String city;

    private String state;

    private String postalCode;
  }
}
