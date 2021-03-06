package gov.va.api.health.dataquery.service.controller.practitionerrole;

import gov.va.api.lighthouse.datamart.DatamartCoding;
import gov.va.api.lighthouse.datamart.DatamartReference;
import gov.va.api.lighthouse.datamart.HasReplaceableId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatamartPractitionerRole implements HasReplaceableId {
  @Builder.Default private String objectType = "PractitionerRole";

  @Builder.Default private int objectVersion = 1;

  private String cdwId;

  private Optional<DatamartReference> practitioner;

  private Boolean active;

  private Optional<String> npi;

  private Optional<DatamartReference> managingOrganization;

  private List<DatamartCoding> role;

  private List<DatamartPractitionerRole.Specialty> specialty;

  private List<DatamartReference> location;

  private List<Telecom> telecom;

  private Optional<String> healthCareService;

  /** Lazy initialization. */
  public Optional<String> healthCareService() {
    if (healthCareService == null) {
      healthCareService = Optional.empty();
    }
    return healthCareService;
  }

  /** Lazy initialization. */
  public List<DatamartReference> location() {
    if (location == null) {
      location = new ArrayList<>();
    }
    return location;
  }

  /** Lazy initialization. */
  public Optional<DatamartReference> managingOrganization() {
    if (managingOrganization == null) {
      managingOrganization = Optional.empty();
    }
    return managingOrganization;
  }

  /** Lazy initialization. */
  public Optional<String> npi() {
    if (npi == null) {
      npi = Optional.empty();
    }
    return npi;
  }

  /** Lazy initialization. */
  public Optional<DatamartReference> practitioner() {
    if (practitioner == null) {
      practitioner = Optional.empty();
    }
    return practitioner;
  }

  /** Lazy initialization. */
  public List<DatamartCoding> role() {
    if (role == null) {
      role = new ArrayList<>();
    }
    return role;
  }

  /** Lazy initialization. */
  public List<DatamartPractitionerRole.Specialty> specialty() {
    if (specialty == null) {
      specialty = new ArrayList<>();
    }
    return specialty;
  }

  /** Lazy initialization. */
  public List<Telecom> telecom() {
    if (telecom == null) {
      telecom = new ArrayList<>();
    }
    return telecom;
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class Specialty {
    private Optional<String> providerType;

    private Optional<String> classification;

    private Optional<String> areaOfSpecialization;

    private Optional<String> vaCode;

    private Optional<String> x12Code;

    private Optional<String> specialtyCode;

    /** Lazy initialization. */
    public Optional<String> areaOfSpecialization() {
      if (areaOfSpecialization == null) {
        areaOfSpecialization = Optional.empty();
      }
      return areaOfSpecialization;
    }

    /** Lazy initialization. */
    public Optional<String> classification() {
      if (classification == null) {
        classification = Optional.empty();
      }
      return classification;
    }

    /** Lazy initialization. */
    public Optional<String> providerType() {
      if (providerType == null) {
        providerType = Optional.empty();
      }
      return providerType;
    }

    /** Lazy Initialization. */
    public Optional<String> specialtyCode() {
      if (specialtyCode == null) {
        specialtyCode = Optional.empty();
      }
      return specialtyCode;
    }

    /** Lazy initialization. */
    public Optional<String> vaCode() {
      if (vaCode == null) {
        vaCode = Optional.empty();
      }
      return vaCode;
    }

    /** Lazy initialization. */
    public Optional<String> x12Code() {
      if (x12Code == null) {
        x12Code = Optional.empty();
      }
      return x12Code;
    }
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class Telecom {
    private System system;

    private Optional<Use> use;

    private String value;

    /** Lazy initialization. */
    public Optional<Use> use() {
      if (use == null) {
        use = Optional.empty();
      }
      return use;
    }

    @SuppressWarnings("JavaLangClash")
    public enum System {
      phone,
      fax,
      pager,
      email
    }

    public enum Use {
      home,
      work,
      mobile
    }
  }
}
