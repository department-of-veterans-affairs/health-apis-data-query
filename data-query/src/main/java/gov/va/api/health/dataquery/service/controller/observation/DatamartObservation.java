package gov.va.api.health.dataquery.service.controller.observation;

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
}

// "cdwId": "800001973863:A",
// "status": "final",
// "category": "laboratory",
// "code": {
// "system": "http:\/\/loinc.org",
// "code": "1989-3",
// "display": "VITAMIN D,25-OH,TOTAL",
// "text": "VITAMIN D,25-OH,TOTAL"
// },
// "subject": {
// "type": "Patient",
// "reference": "1002003004V666666",
// "display": "VETERAN,AUDIE OBS"
// },
// "encounter": {
// "type": "Encounter",
// "reference": "123454321",
// "display": "Ambulatory"
// },
// "effectiveDateTime": "2012-12-24T14:12:00Z",
// "issued": "2012-12-26T19:42:00Z",
// "performer": [{
// "type": "Practitioner",
// "reference": "666000",
// "display": "WELBY,MARCUS MCCOY"
// }, {
// "type": "Organization",
// "reference": "325832",
// "display": "WHITE RIVER JCT VAMROC"
// }],
// "valueQuantity": {
// "value": "111.82",
// "unit": "ng\/mL",
// "system": "http:\/\/unitsofmeasure.org",
// "code": "ng\/mL"
// },
// "valueCodeableConcept": {
// "coding": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "112283007",
// "display": "ESCHERICHIA COLI"
// },
// "text": "ESCHERICHIA COLI"
// },
// "interpretation": "H",
// "comment": "CYTOSPIN:NO ACID-FAST BACILLI SEEN. 01\/02\/2015 \rNO ACID-FAST BACILLI ISOLATED TO
// DATE \rNO ACID-FAST BACILLI ISOLATED AFTER 6 WEEKS BY LABCORP. \r",
// "specimen": {
// "type": "Specimen",
// "reference": "800005563",
// "display": "URINE (CLEAN CATCH)"
// },
// "referenceRange": {
// "high": {
// "value": "100",
// "unit": "ng\/mL",
// "system": "http:\/\/unitsofmeasure.org",
// "code": "ng\/mL"
// },
// "low": {
// "value": "30",
// "unit": "ng\/mL",
// "system": "http:\/\/unitsofmeasure.org",
// "code": "ng\/mL"
// }
// },
// "vitalsComponents": [{
// "code": {
// "system": "http:\/\/loinc.org",
// "code": "8480-6",
// "display": "Systolic blood pressure"
// },
// "valueQuantity": {
// "value": 114,
// "unit": "mm[Hg]",
// "unitSystem": "http:\/\/unitsofmeasure.org",
// "unitCode": "mm[Hg]"
// }
// }, {
// "code": {
// "system": "http:\/\/loinc.org",
// "code": "8462-4",
// "display": "Diastolic blood pressure"
// },
// "valueQuantity": {
// "value": 62,
// "unit": "mm[Hg]",
// "unitSystem": "http:\/\/unitsofmeasure.org",
// "unitCode": "mm[Hg]"
// }
// }],
// "antibioticComponents": [{
// "id": "800011708199",
// "codeText": "CEFAZOLIN-1",
// "code": {
// "text": "CEFAZOLIN-1",
// "system": "http:\/\/loinc.org",
// "code": "76-0",
// "display": "CEFAZOLIN"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708205",
// "codeText": "IMIPENEM",
// "code": {
// "text": "IMIPENEM",
// "system": "http:\/\/loinc.org",
// "code": "279-0",
// "display": "IMIPENEM"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708207",
// "codeText": "CIPROFLOXACIN",
// "code": {
// "text": "CIPROFLOXACIN",
// "system": "http:\/\/loinc.org",
// "code": "185-9",
// "display": "CIPROFLOXACIN"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708208",
// "codeText": "CEFTRIAXONE-3",
// "code": {
// "text": "CEFTRIAXONE-3",
// "system": "http:\/\/loinc.org",
// "code": "141-2",
// "display": "CEFTRIAXONE"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708209",
// "codeText": "AMP\/SULBACTAM",
// "code": {
// "text": "AMP\/SULBACTAM",
// "system": "http:\/\/loinc.org",
// "code": "32-3",
// "display": "AMPICILLIN+SULBACTAM"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708210",
// "codeText": "CEFEPIME-4",
// "code": {
// "text": "CEFEPIME-4",
// "system": "http:\/\/loinc.org",
// "code": "6644-9",
// "display": "CEFEPIME"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708211",
// "codeText": "ERTAPENEM",
// "code": {
// "text": "ERTAPENEM",
// "system": "http:\/\/loinc.org",
// "code": "35801-0",
// "display": "ERTAPENEM"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708212",
// "codeText": "TIGECYCLINE",
// "code": {
// "text": "TIGECYCLINE",
// "system": "http:\/\/loinc.org",
// "code": "42355-8",
// "display": "TIGECYCLINE"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708200",
// "codeText": "AMPICILLIN",
// "code": {
// "text": "AMPICILLIN",
// "system": "http:\/\/loinc.org",
// "code": "28-1",
// "display": "AMPICILLIN"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708213",
// "codeText": "ESBL",
// "code": {
// "text": "ESBL",
// "system": "http:\/\/loinc.org",
// "code": "6984-9",
// "display": "BETA LACTAMASE.EXTENDED SPECTRUM"
// },
// "valueCodeableConcept": {
// "code": "-"
// }
// }, {
// "id": "800011708198",
// "codeText": "GENTAMICIN",
// "code": {
// "text": "GENTAMICIN",
// "system": "http:\/\/loinc.org",
// "code": "267-5",
// "display": "GENTAMICIN"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708206",
// "codeText": "AZTREONAM",
// "code": {
// "text": "AZTREONAM",
// "system": "http:\/\/loinc.org",
// "code": "44-8",
// "display": "AZTREONAM"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708202",
// "codeText": "SXT (BACTRIM)",
// "code": {
// "text": "SXT (BACTRIM)",
// "system": "http:\/\/loinc.org",
// "code": "516-5",
// "display": "TRIMETHOPRIM+SULFAMETHOXAZOLE"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "R",
// "display": "Resistant"
// }
// }, {
// "id": "800011708201",
// "codeText": "TOBRAMYCIN",
// "code": {
// "text": "TOBRAMYCIN",
// "system": "http:\/\/loinc.org",
// "code": "508-2",
// "display": "TOBRAMYCIN"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708203",
// "codeText": "AMIKACIN",
// "code": {
// "text": "AMIKACIN",
// "system": "http:\/\/loinc.org",
// "code": "12-5",
// "display": "AMIKACIN"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }, {
// "id": "800011708204",
// "codeText": "CEFOXITIN",
// "code": {
// "text": "CEFOXITIN",
// "system": "http:\/\/loinc.org",
// "code": "116-4",
// "display": "CEFOXITIN"
// },
// "valueCodeableConcept": {
// "system": "http:\/\/snomed.info\/sct",
// "code": "S",
// "display": "Sensitive"
// }
// }],
// "mycobacteriologyComponents": {
// "code": {
// "text": "Acid Fast Stain"
// },
// "valueText": {
// "text": "Concentrate Negative"
// }
// },
// "bacteriologyComponents": {
// "code": {
// "text": "Sputum Screen"
// },
// "valueText": {
// "text": "GOOD QUALITY SPECIMEN BY GRAM STAIN EVALUATION "
// }
// }
//
// }
