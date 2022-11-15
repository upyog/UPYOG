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

    @JsonProperty("deathDtlId")
    private String deathDtlId ; 

    @JsonProperty("residenceLb")
    private String residenceLb;

    @JsonProperty("residencePlaceType")
    private String residencePlaceType;

    @JsonProperty("residenceDist")
    private String residenceDist;

    @JsonProperty("residenceState")
    private String residenceState;

    @JsonProperty("religion")
    private String religion;

    @JsonProperty("otherReligion")
    private String otherReligion;

    @JsonProperty("occupation")
    private String occupation;

    @JsonProperty("occupationOther")
    private String occupationOther;

    @JsonProperty("medAttentionType")
    private String medAttentionType;

    @JsonProperty("deathMedCertified")
    private Integer deathMedCertified;

    @JsonProperty("deathcauseMain")
    private String deathcauseMain;

    @JsonProperty("deathcauseSub")
    private String deathcauseSub;

    @JsonProperty("deathcauseOther")
    private String deathcauseOther;

    @JsonProperty("deliveryDeath")
    private Integer deliveryDeath;

    @JsonProperty("smokingNumYears")
    private String smokingNumYears;

    @JsonProperty("tobaccoNumYears")
    private String tobaccoNumYears;

    @JsonProperty("arecanutNumYears")
    private String arecanutNumYears;

    @JsonProperty("alcoholnumYears")
    private String alcoholnumYears;
    
}
