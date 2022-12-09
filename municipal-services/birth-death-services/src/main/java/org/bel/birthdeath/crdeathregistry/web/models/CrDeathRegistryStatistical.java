package org.bel.birthdeath.crdeathregistry.web.models;

// import java.sql.Timestamp;

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
     * on 28.11.2022
     */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrDeathRegistryStatistical {

    
    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @JsonProperty("deathDtlId")
    private String deathDtlId ; 

    @JsonProperty("residenceLocalBody")
    private String residenceLocalBody;

    @JsonProperty("residencePlaceType")
    private String residencePlaceType;

    @JsonProperty("residenceDistrict")
    private String residenceDistrict;

    @JsonProperty("residenceState")
    private String residenceState;

    @JsonProperty("religion")
    private String religion;

    @JsonProperty("religionOther")
    private String religionOther;

    @JsonProperty("occupation")
    private String occupation;

    @JsonProperty("occupationOther")
    private String occupationOther;

    @JsonProperty("medicalAttentionType")
    private String medicalAttentionType;

    @JsonProperty("deathMedicallyCertified")
    private Integer deathMedicallyCertified;

    @JsonProperty("deathCauseMain")
    private String deathCauseMain;

    @JsonProperty("deathCauseSub")
    private String deathCauseSub;

    @JsonProperty("deathCauseOther")
    private String deathCauseOther;

    @JsonProperty("deathDuringDelivery")
    private Integer deathDuringDelivery;

    @JsonProperty("smokingNumYears")
    private Integer smokingNumYears;

    @JsonProperty("tobaccoNumYears")
    private Integer tobaccoNumYears;

    @JsonProperty("arecanutNumYears")
    private Integer arecanutNumYears;

    @JsonProperty("alcoholNumYears")
    private Integer alcoholNumYears;

    //Rakhi S on 07.12.2022
    @JsonProperty("nationality")
    private String nationality;
    
}
