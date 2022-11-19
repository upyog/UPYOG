package org.bel.birthdeath.crdeath.web.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
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

    @JsonProperty("registrationUnit")
    private String registrationUnit;

    @JsonProperty("tenantId")
    private String tenantId ;

    @JsonProperty("correctDeathDateKnown")
    private Integer correctDeathDateKnown ;

    @JsonProperty("dateOfDeath")
    private Long dateOfDeath ;

    @JsonProperty("timeOfDeath")
    private Integer  timeOfDeath ;

    @JsonProperty("timeOfDeathUnit")
    private String timeOfDeathUnit ;

    @JsonProperty("dateOfDeath1")
    private Long dateOfDeath1;

    @JsonProperty("timeOfDeath1")
    private Integer timeOfDeath1 ;

    @JsonProperty("timeOfDeathUnit1")
    private String timeOfDeathUnit1 ;

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

    @JsonProperty("deceasedAadharSubmitted")
    private Integer deceasedAadharSubmitted ;

    @JsonProperty("deceasedAadharNumber")
    private String deceasedAadharNumber ;

    @JsonProperty("deceasedGender")
    private String deceasedGender ;

    @JsonProperty("age")
    private Integer age ;

    @JsonProperty("ageUnit")
    private String ageUnit ;

    @JsonProperty("dateOfBirth")
    private Long dateOfBirth ;

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
    private String  informantNameMl ;

    @JsonProperty("informantAadharSubmitted")
    private String  informantAadharSubmitted ;

    @JsonProperty("informantAadharNo")
    private String  informantAadharNo ;

    @JsonProperty("informantMobileNo")
    private String  informantMobileNo ;

    @JsonProperty("generalRemarks")
    private String   generalRemarks ;

    @JsonProperty("applicationStatus")
    private String   applicationStatus ;

    @JsonProperty("submittedOn")
    private String  submittedOn ;    

    @JsonProperty("placeBurial")
    private String  placeBurial;

    @JsonProperty("placeBurialInstitutionType")
    private String   placeBurialInstitutionType ;

    @JsonProperty("placePurialInstitutionName")
    private String  placePurialInstitutionName ;

    @JsonProperty("registrationNo")
    private String  registrationNo ;

    @JsonProperty("ipNo")
    private String  ipNo ;

    @JsonProperty("opNo")
    private String  opNo ;

    @JsonProperty("maleDependentType")
    private String  maleDependentType ;

    @JsonProperty("maleDependentTitle")
    private String  maleDependentTitle ;

    @JsonProperty("maleDependentNameEn")
    private String  maleDependentNameEn ;

    @JsonProperty("maleDependentNameMl")
    private String  maleDependentNameMl ;

    @JsonProperty("maleDependentAadharNo")
    private String  maleDependentAadharNo ;

    @JsonProperty("maleDependentMobileNo")
    private String  maleDependentMobileNo ;

    @JsonProperty("maleDependentMailId")
    private String  maleDependentMailId ;

    @JsonProperty("femaleDependentType")
    private String  femaleDependentType ;

    @JsonProperty("femaleDependentTitle")
    private String  femaleDependentTitle ;

    @JsonProperty("femaleDependentNameEn")
    private String  femaleDependentNameEn ;

    @JsonProperty("femaleDependentNameMl")
    private String  femaleDependentNameMl ;

    @JsonProperty("femaleDependentAadharNo")
    private String  femaleDependentAadharNo;

    @JsonProperty("femaleDependentMobileNo")
    private String  femaleDependentMobileNo ;

    @JsonProperty("femaleDependentMailId")
    private String  femaleDependentMailId ;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;    

    @JsonProperty("statisticalInfo")
    private CrDeathStatistical statisticalInfo;

    @JsonProperty("addressInfo")
    @Valid
    private List<CrDeathAddressInfo>  addressInfo;

    public CrDeathDtl addCrDeathDtl(CrDeathAddressInfo crDeathAddressInfo) {
        if (addressInfo == null) {
            addressInfo = new ArrayList<>();
        }
        addressInfo.add(crDeathAddressInfo);

        return this;
    }

   
}
