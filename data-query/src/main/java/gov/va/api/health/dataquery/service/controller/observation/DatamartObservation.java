package gov.va.api.health.dataquery.service.controller.observation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class DatamartObservation implements HasReplaceableId {
  private String objectType;

  private int objectVersion;

  private String cdwId;

  private Status status;

  private Category category;

  private Optional<CodeableConcept> code;
  /** Lazy getter. */
  public Optional<CodeableConcept> code() {
    if (code == null) {
      code = Optional.empty();
    }
    return code;
  }

  private Optional<DatamartReference> subject;
  /** Lazy getter. */
  public Optional<DatamartReference> subject() {
    if (subject == null) {
      subject = Optional.empty();
    }
    return subject;
  }

  private Optional<DatamartReference> encounter;
  /** Lazy getter. */
  public Optional<DatamartReference> encounter() {
    if (encounter == null) {
      encounter = Optional.empty();
    }
    return encounter;
  }

  private Optional<Instant> effectiveDateTime;
  /** Lazy getter. */
  public Optional<Instant> effectiveDateTime() {
    if (effectiveDateTime == null) {
      effectiveDateTime = Optional.empty();
    }
    return effectiveDateTime;
  }

  private Optional<Instant> issued;
  /** Lazy getter. */
  public Optional<Instant> issued() {
    if (issued == null) {
      issued = Optional.empty();
    }
    return issued;
  }

  private List<DatamartReference> performer;
  /** Lazy getter. */
  public List<DatamartReference> performer() {
    if (performer == null) {
      performer = new ArrayList<>();
    }
    return performer;
  }

  private Optional<Quantity> valueQuantity;
  /** Lazy getter. */
  public Optional<Quantity> valueQuantity() {
    if (valueQuantity == null) {
      valueQuantity = Optional.empty();
    }
    return valueQuantity;
  }

  private Optional<CodeableConcept> valueCodeableConcept;
  /** Lazy getter. */
  public Optional<CodeableConcept> valueCodeableConcept() {
    if (valueCodeableConcept == null) {
      valueCodeableConcept = Optional.empty();
    }
    return valueCodeableConcept;
  }

  private String interpretation;

  private String comment;

  private Optional<DatamartReference> specimen;
  /** Lazy getter. */
  public Optional<DatamartReference> specimen() {
    if (specimen == null) {
      specimen = Optional.empty();
    }
    return specimen;
  }

  private Optional<ReferenceRange> referenceRange;
  /** Lazy getter. */
  public Optional<ReferenceRange> referenceRange() {
    if (referenceRange == null) {
      referenceRange = Optional.empty();
    }
    return referenceRange;
  }

  private List<VitalsComponent> vitalsComponents;
  /** Lazy getter. */
  public List<VitalsComponent> vitalsComponents() {
    if (vitalsComponents == null) {
      vitalsComponents = new ArrayList<>();
    }
    return vitalsComponents;
  }

  private List<AntibioticComponent> antibioticComponents;
  /** Lazy getter. */
  public List<AntibioticComponent> antibioticComponents() {
    if (antibioticComponents == null) {
      antibioticComponents = new ArrayList<>();
    }
    return antibioticComponents;
  }

  private Optional<BacteriologyComponent> mycobacteriologyComponents;
  /** Lazy getter. */
  public Optional<BacteriologyComponent> mycobacteriologyComponents() {
    if (mycobacteriologyComponents == null) {
      mycobacteriologyComponents = Optional.empty();
    }
    return mycobacteriologyComponents;
  }

  private Optional<BacteriologyComponent> bacteriologyComponents;
  /** Lazy getter. */
  public Optional<BacteriologyComponent> bacteriologyComponents() {
    if (bacteriologyComponents == null) {
      bacteriologyComponents = Optional.empty();
    }
    return bacteriologyComponents;
  }

  public enum Status {
    registered,
    preliminary,
    @JsonProperty("final")
    _final,
    amended,
    cancelled,
    @JsonProperty("entered-in-error")
    entered_in_error,
    unknown,
    @JsonProperty("data-absent-reason:unsupported")
    data_absent_reason_unsupported
  }

  public enum Category {
    @JsonProperty("social-history")
    social_history,
    @JsonProperty("vital-signs")
    vital_signs,
    imaging,
    laboratory,
    procedure,
    survey,
    exam,
    therapy
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class AntibioticComponent {
    String id;

    String codeText;

    Optional<CodeableConcept> code;
    /** Lazy getter. */
    public Optional<CodeableConcept> code() {
      if (code == null) {
        code = Optional.empty();
      }
      return code;
    }

    Optional<DatamartCoding> valueCodeableConcept;
    /** Lazy getter. */
    public Optional<DatamartCoding> valueCodeableConcept() {
      if (valueCodeableConcept == null) {
        valueCodeableConcept = Optional.empty();
      }
      return valueCodeableConcept;
    }
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class BacteriologyComponent {
    Optional<Text> code;
    /** Lazy getter. */
    public Optional<Text> code() {
      if (code == null) {
        code = Optional.empty();
      }
      return code;
    }

    Optional<Text> valueText;
    /** Lazy getter. */
    public Optional<Text> valueText() {
      if (valueText == null) {
        valueText = Optional.empty();
      }
      return valueText;
    }
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class CodeableConcept {
    Optional<DatamartCoding> coding;

    private String text;

    public Optional<DatamartCoding> coding() {
      if (coding == null) {
        coding = Optional.empty();
      }
      return coding;
    }

    void setSystem(String system) {
      if (coding().isEmpty()) {
        coding(Optional.of(DatamartCoding.builder().build()));
      }
      coding().get().system(Optional.of(system));
    }

    void setCode(String code) {
      if (coding().isEmpty()) {
        coding(Optional.of(DatamartCoding.builder().build()));
      }
      coding().get().code(Optional.of(code));
    }

    void setDisplay(String display) {
      if (coding().isEmpty()) {
        coding(Optional.of(DatamartCoding.builder().build()));
      }
      coding().get().display(Optional.of(display));
    }
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class Quantity {
    String value;

    String unit;

    String system;

    String code;

    void setUnitSystem(String system) {
      system(system);
    }

    void setUnitCode(String code) {
      code(code);
    }
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class ReferenceRange {
    private Optional<Quantity> high;
    /** Lazy getter. */
    public Optional<Quantity> high() {
      if (high == null) {
        high = Optional.empty();
      }
      return high;
    }

    private Optional<Quantity> low;
    /** Lazy getter. */
    public Optional<Quantity> low() {
      if (low == null) {
        low = Optional.empty();
      }
      return low;
    }
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class Text {
    private String text;
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  static final class VitalsComponent {
    private Optional<DatamartCoding> code;
    /** Lazy getter. */
    public Optional<DatamartCoding> code() {
      if (code == null) {
        code = Optional.empty();
      }
      return code;
    }

    private Optional<Quantity> valueQuantity;
    /** Lazy getter. */
    public Optional<Quantity> valueQuantity() {
      if (valueQuantity == null) {
        valueQuantity = Optional.empty();
      }
      return valueQuantity;
    }
  }
}
