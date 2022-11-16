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

    @JsonProperty("registrationUnit")
    private String registrationUnit;

    @JsonProperty("tenantId")
    private String tenantId ;

    @JsonProperty("correctDeathDateKnown")
    private String correctDeathDateKnown ;

    @JsonProperty("dateOfDeath")
    private Long dateOfDeath ;

    @JsonProperty("timeOfDeath")
    private Integer  timeOfDeath ;

    @JsonProperty("timeOfDeathUnit")
    private Integer timeOfDeathUnit ;

    @JsonProperty("dateOfDeath1")
    private Long dateOfDeath1;

    @JsonProperty("time_of_death_1")
    private Integer time_of_death_1 ;

    @JsonProperty("timeofdeath_unit1")
    private Long timeofdeath_unit1 ;

    @JsonProperty("deceased_identified")
    private Integer deceased_identified ;

    @JsonProperty("deceased_title")
    private String deceased_title ;

    @JsonProperty("deceased_firstname_en")
    private String deceased_firstname_en ;

    @JsonProperty("deceased_firstname_ml")
    private String deceased_firstname_ml ;

    @JsonProperty("deceased_middlename_en")
    private String deceased_middlename_en ;

    @JsonProperty("deceased_middlename_ml")
    private String deceased_middlename_ml ;

    @JsonProperty("deceased_lastname_en")
    private String deceased_lastname_en ;

    @JsonProperty("deceased_lastname_ml")
    private String deceased_lastname_ml ;

    @JsonProperty("deceased_aadhar_submitted")
    private String deceased_aadhar_submitted ;

    @JsonProperty("deceased_aadhar_number")
    private String deceased_aadhar_number ;

    @JsonProperty("deceased_gender")
    private String deceased_gender ;

    @JsonProperty("age")
    private Integer age ;

    @JsonProperty("age_unit")
    private Integer age_unit ;

    @JsonProperty("dateofbirth")
    private Long dateofbirth ;

    @JsonProperty("death_place")
    private String death_place ;

    @JsonProperty("death_place_type")
    private String death_place_type ;

    @JsonProperty("death_place_inst_id")
    private String death_place_inst_id ;

    @JsonProperty("death_place_office_name")
    private String death_place_office_name ;

    @JsonProperty("death_place_other_ml")
    private String death_place_other_ml ;

    @JsonProperty("death_place_other_en")
    private String death_place_other_en ;

    @JsonProperty("informant_title")
    private String  informant_title ;

    @JsonProperty("informant_name_en")
    private String  informant_name_en ;

    @JsonProperty("informant_name_ml")
    private String  informant_name_ml ;

    @JsonProperty("informant_aadhar_submitted")
    private String  informant_aadhar_submitted ;

    @JsonProperty("informant_aadhar_no")
    private String  informant_aadhar_no ;

    @JsonProperty("informant_mobile_no")
    private String  informant_aadhar_no ;

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
