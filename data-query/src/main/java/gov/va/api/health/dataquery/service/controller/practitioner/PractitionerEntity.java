package gov.va.api.health.dataquery.service.controller.practitioner;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.dataquery.service.controller.datamart.DatamartEntity;
import java.util.Optional;
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
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;

@Data
@Entity
@Builder
@Table(name = "Practitioner", schema = "app")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PractitionerEntity implements DatamartEntity {
  @Id
  @Column(name = "CDWID")
  @EqualsAndHashCode.Include
  private String cdwId;

  @Column(name = "NPI", nullable = true)
  private String npi;

  @Column(name = "FamilyName", nullable = true)
  private String familyName;

  @Column(name = "GivenName", nullable = true)
  private String givenName;

  @Lob
  @Basic(fetch = FetchType.EAGER)
  @Column(name = "Practitioner")
  private String payload;

  @SneakyThrows
  DatamartPractitioner asDatamartPractitioner() {
    DatamartPractitioner dm =
        JacksonConfig.createMapper().readValue(payload, DatamartPractitioner.class);

    if (dm.practitionerRole().isPresent()
        && dm.practitionerRole().get().managingOrganization().isPresent() && dm.practitionerRole().get().managingOrganization().get().type().isEmpty()) {
      // Hack... make sure reference type is populated
      dm.practitionerRole().get().managingOrganization().get().type(Optional.of("Organization"));
    }

    if (dm.practitionerRole().isPresent() && dm.practitionerRole().get().location() != null){
      for (int i = 0; i < dm.practitionerRole().get().location().size(); i++){
        if (dm.practitionerRole().get().location().get(i).type().isEmpty()) {
          // Hack... make sure reference type is populated
          dm.practitionerRole().get().location().get(0).type(Optional.of("Location"));
        }
      }
    }

    return dm;
  }
}
