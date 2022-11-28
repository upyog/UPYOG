package org.bel.birthdeath.crdeath.web.models;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;

/**
     * Creates statistical details model 
     * Rakhi S IKM
     * 
     */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrDeathStatistical {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @JsonProperty("death_dtl_id")
    private String deathDtlId ; 

    @JsonProperty("residencelocalbody")
    private String residenceLocalbody;

    @JsonProperty("residence_place_type")
    private String residencePlaceType;

    @JsonProperty("residencedistrict")
    private String residenceDistrict;

    @JsonProperty("residencestate")
    private String residenceState;

    @JsonProperty("religion")
    private String religion;

    @JsonProperty("religion_other")
    private String religionOther;

    @JsonProperty("occupation")
    private String occupation;

    @JsonProperty("occupation_other")
    private String occupationOther;

    @JsonProperty("medical_attention_type")
    private String medicalAttentionType;

    @JsonProperty("death_medically_certified")
    private Integer deathMedicallyCertified;

    @JsonProperty("death_cause_main")
    private String deathCauseMain;

    @JsonProperty("death_cause_sub")
    private String deathCauseSub;

    @JsonProperty("death_cause_other")
    private String deathCauseOther;

    @JsonProperty("death_during_delivery")
    private Integer deathDuringDelivery;

    @JsonProperty("smoking_num_years")
    private String smokingNumYears;

    @JsonProperty("tobacco_num_years")
    private String tobaccoNumYears;

    @JsonProperty("arecanut_num_years")
    private String arecanutNumYears;

    @JsonProperty("alcohol_num_years")
    private String alcoholNumYears;
    
}
