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
public class CrDeathDtl {
    
    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @JsonProperty("regUnit")
    private String regUnit;

    @JsonProperty("tenantId")
    private String tenantId ;

    @JsonProperty("deathType")
    private String deathType ;

    @JsonProperty("dateofDeath")
    private Timestamp dateDeath ;

    @JsonProperty("deathTime")
    private Integer  timeDeath ;

    @JsonProperty("deathTimeUnit")
    private Integer timeDeathUnit ;

    @JsonProperty("dateofDeath1")
    private Timestamp dateDeath1;

    @JsonProperty("deathTime1")
    private Integer timeDeath1 ;

    @JsonProperty("deathTimeUnit1")
    private Timestamp timeDeathUnit1 ;

    @JsonProperty("deceasedIdentified")
    private Integer deceasedIdentified ;

    @JsonProperty("deceasedTitle")
    private String deceasedTitle ;

    @JsonProperty("deceasedFirstNameEn")
    private String deceasedFirstNameEn ;

    @JsonProperty("deceasedFirstNameMl")
    private String deceasedFirstNameMl ;

    @JsonProperty("deceasedMiddleNameEn")
    private String deceasedMiddleNameEn ;

    @JsonProperty("deceasedMiddleNameMl")
    private String deceasedMiddleNameMl ;

    @JsonProperty("deceasedLastNameEn")
    private String deceasedLastNameEn ;

    @JsonProperty("deceasedLastNameMl")
    private String deceasedLastNameMl ;

    @JsonProperty("deceasedAadharSubmtd")
    private String deceasedAadharSubmtd ;

    @JsonProperty("deceasedAadharNo")
    private String deceasedAadharNo ;

    @JsonProperty("deceasedGender")
    private String deceasedGender ;

    @JsonProperty("age")
    private Integer age ;

    @JsonProperty("ageUnit")
    private Integer ageUnit ;

    @JsonProperty("dateOfBirth")
    private Timestamp dateOfBirth ;

    @JsonProperty("deathPlace")
    private String deathPlace ;

    @JsonProperty("deathPlaceType")
    private String deathPlaceType ;

    @JsonProperty("deathPlaceInstId")
    private String deathPlaceInstId ;

    @JsonProperty("deathPlaceOfficeName")
    private String deathPlaceOfficeName ;

    @JsonProperty("deathPlaceOtherMl")
    private String deathPlaceOtherMl ;

    @JsonProperty("deathPlaceOtherEn")
    private String deathPlaceOtherEn ;

    @JsonProperty("informantTitle")
    private String  informantTitle ;

    @JsonProperty("informantNameEn")
    private String  informantNameEn ;

    @JsonProperty("informantNameMl")
    private String  informantNameml ;

    @JsonProperty("informantAadharSubmtd")
    private String  informantAadharSubmtd ;

    @JsonProperty("informantAadharNo")
    private String   informantAadharNo ;

    @JsonProperty("genRemarks")
    private String   genRemarks ;

    @JsonProperty("applicationStatus")
    private String   applicationStatus ;

    @JsonProperty("submittedOn")
    private String  submittedOn ;

    @JsonProperty("placeBurial")
    private String  placeBurial;

    @JsonProperty("placeBurialInstType")
    private String   placeBurialInstType ;

    @JsonProperty("placeBurialInstName")
    private String  placeBurialInstName ;

    @JsonProperty("registrationNo")
    private String  registrationNo ;

    @JsonProperty("ipNo")
    private String  ipNo ;

    @JsonProperty("opNo")
    private String  opNo ;

    @JsonProperty("maleDepntType")
    private String  maleDepntType ;

    @JsonProperty("maleDepntTitle")
    private String  maleDepntTitle ;

    @JsonProperty("maleDepntNameEn")
    private String  maleDepntNameEn ;

    @JsonProperty("maleDepntNamel")
    private String  maleDepntNamel ;

    @JsonProperty("maleDepntAadharNo")
    private String  maleDepntAadharNo ;

    @JsonProperty("maleDepntMobNo")
    private String  maleDepntMobNo ;

    @JsonProperty("maleDepntMailId")
    private String  maleDepntMailId ;

    @JsonProperty("femaleDepntType")
    private String  femaleDepntType ;

    @JsonProperty("femaleDepntTitle")
    private String  femaleDepntTitle ;

    @JsonProperty("femaleDepntNameEn")
    private String  femaleDepntNameEn ;

    @JsonProperty("femaleDepntNameMl")
    private String  femaleDepntNameMl ;

    @JsonProperty("femaleDepntAadharNo")
    private String  femaleDepntAadharNo;

    @JsonProperty("femaleDepntMobNo")
    private String  femaleDepntMobNo ;

    @JsonProperty("femaleDepntMailId")
    private String  femaleDepntMailId ;
}
