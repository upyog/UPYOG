package org.ksmart.birth.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.common.model.AuditDetails;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParentsDetail {
    @Size(max = 64)
    @JsonProperty("motherUuid")
    private String motherUuid;
    @Size(max = 2000)
    @JsonProperty("motherFirstNameEn")
    private String firstNameEn;
    @Size(max = 2000)
    @JsonProperty("motherFirstNameMl")
    private String firstNameMl;
    @Size(max = 64)
    @JsonProperty("motherAadhar")
    private String motherAadhar;
    @JsonProperty("motherMarriageAge")
    private Integer motherAgeMarriage;
    @Size(max = 64)
    @JsonProperty("motherMarriageBirth")
    private Integer motherAgeDelivery;
    @Size(max = 64)
    @JsonProperty("motherEducation")
    private String motherEducationid;

    @Size(max = 300)
    @JsonProperty("motherEducationEn")
    private String motherEducationidEn;

    @Size(max = 300)
    @JsonProperty("motherEducationMl")
    private String motherEducationidMl;

    @Size(max = 64)
    @JsonProperty("motherProfession")
    private String motherProffessionid;

    @Size(max = 300)
    @JsonProperty("motherProfessionEn")
    private String motherProffessionidEn;

    @Size(max = 300)
    @JsonProperty("motherProfessionMl")
    private String motherProffessionidMl;

    @Size(max = 64)
    @JsonProperty("motherNationality")
    private String motherNationalityid;

    @Size(max = 300)
    @JsonProperty("motherNationalityEn")
    private String motherNationalityidEn;

    @Size(max = 64)
    @JsonProperty("motherNationalityMl")
    private String motherNationalityidMl;

    @Size(max = 64)
    @JsonProperty("orderofChildren")
    private Integer motherOrderCurChild;
    @Size(max = 64)
    @JsonProperty("fatherUuid")
    private String fatherUuid;
    @Size(max = 64)
    @JsonProperty("fatherAadhar")
    private String fatherAadharno;
    @Size(max = 64)
    @JsonProperty("ismotherInfo")
    private Boolean isMotherInfoMissing;
    @Size(max = 64)
    @JsonProperty("isfatherInfo")
    private Boolean isFatherInfoMissing;
    @Size(max = 64)
    @JsonProperty("fatherFirstNameEn")
    private String fatherFirstNameEn;
    @Size(max = 64)
    @JsonProperty("fatherFirstNameMl")
    private String fatherFirstNameMl;
    @Size(max = 64)
    @JsonProperty("fatherNationality")
    private String fatherNationalityid;
    @Size(max = 300)
    @JsonProperty("fatherNationalityEn")
    private String fatherNationalityidEn;
    @Size(max = 300)
    @JsonProperty("fatherNationalityMl")
    private String fatherNationalityidMl;

    @Size(max = 64)
    @JsonProperty("fatherEducation")
    private String fatherEucationid;

    @Size(max = 300)
    @JsonProperty("fatherEducationEn")
    private String fatherEucationidEn;

    @Size(max = 300)
    @JsonProperty("fatherEducationMl")
    private String fatherEucationidMl;

    @Size(max = 64)
    @JsonProperty("fatherProfession")
    private String fatherProffessionid;

    @Size(max = 300)
    @JsonProperty("fatherProfessionEn")
    private String fatherProffessionidEn;

    @Size(max = 300)
    @JsonProperty("fatherProfessionMl")
    private String fatherProffessionidMl;

    @Size(max = 64)
    @JsonProperty("Religion")
    private String religionId;

    @Size(max = 300)
    @JsonProperty("ReligionEn")
    private String religionIdEn;

    @Size(max = 300)
    @JsonProperty("ReligionMl")
    private String religionIdMl;

    @Size(max = 64)
    @JsonProperty("fatherMobile")
    private String familyMobileNo;
    @Size(max = 64)
    @JsonProperty("fatherEmail")
    private String familyEmailid;
    @Size(max = 64)
    @JsonProperty("fatherPassportNo")
    private String fatherPassport;
    @Size(max = 64)
    @JsonProperty("motherPassportNo")
    private String motherPassport;
    @Size(max = 64)
    @JsonProperty("motherBioAdopt")
    private String motherBioAdopt;
    @Size(max = 64)
    @JsonProperty("motherMaritalStatus")
    private String motherMaritalStatus;

    @Size(max = 300)
    @JsonProperty("motherMaritalStatusEn")
    private String motherMaritalStatusEn;

    @Size(max = 300)
    @JsonProperty("motherMaritalStatusMl")
    private String motherMaritalStatusMl;

    @JsonProperty("ageMarriageStatusHide")// if married true
    private Boolean ageMarriageStatusHide;
    @Size(max = 64)
    @JsonProperty("fatherBioAdopt")
    private String fatherBioAdopt;
    @Size(max = 2500)
    @JsonProperty("addressOfMother")
    private String addressOfMother;
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
    
    @Size(max = 1000)
    @JsonProperty("townOrVillagePresent")
    private String townOrVillagePresent;


}
