package gov.va.api.health.dataquery.service.controller.condition;

import gov.va.api.health.dataquery.service.controller.DatamartSupport;
import gov.va.api.lighthouse.datamart.DatamartEntity;
import gov.va.api.lighthouse.datamart.Payload;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Datamart Condition representing the following table.
 *
 * <pre>
 * CREATE TABLE [app].[Condition](
 *  [CDWId] [varchar](50) NOT NULL,
 *  [CdwIdNumber] [bigint] NOT NULL,
 *  [CdwIdResourceCode] [char](1) NULL,
 *  [PatientFullICN] [varchar](50) NOT NULL,
 *  [AsserterCDWId] [int] NULL,
 *  [Category] [varchar](50) NULL,
 *  [ClinicalStatus] [varchar](50) NULL,
 *  [DateRecorded] [datetime2](0) NULL,
 *  [EncounterCDWId] [bigint] NULL,
 *  [OnSet] [datetime2](0) NULL,
 *  [Condition] [varchar](max) NULL,
 *  [ETLBatchId] [int] NULL,
 *  [ETLCreateDate] [datetime2](0) NULL,
 *  [ETLEditDate] [datetime2](0) NULL,
 * </pre>
 */
@Data
@Entity
@Builder
@Table(name = "Condition", schema = "app")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConditionEntity implements DatamartEntity {
  @Column(name = "CdwIdNumber")
  @EqualsAndHashCode.Include
  private BigInteger cdwIdNumber;

  @Column(name = "CdwIdResourceCode")
  @EqualsAndHashCode.Include
  private char cdwIdResourceCode;

  @Id
  @Column(name = "CDWId")
  @EqualsAndHashCode.Include
  private String cdwId;

  @Column(name = "PatientFullICN")
  private String icn;

  @Column(name = "Category")
  private String category;

  @Column(name = "ClinicalStatus")
  private String clinicalStatus;

  @Column(name = "Condition")
  @Basic(fetch = FetchType.EAGER)
  @Lob
  private String payload;

  static Sort naturalOrder() {
    return Sort.by("cdwId").ascending();
  }

  DatamartCondition asDatamartCondition() {
    return toPayload().deserialize();
  }

  @Override
  public Payload<DatamartCondition> toPayload() {
    return Payload.ofType(DatamartCondition.class)
        .json(payload())
        .cdwId(cdwId())
        .mapper(DatamartSupport.mapper())
        .build();
  }
}
