package org.ksmart.birth.ksmartbirthapplication.model.newbirth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.common.model.AuditDetails;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KsmartBirthParentDetail {

    @Size(max = 64)
    @JsonProperty("motherUuid")
    private String motherUuid;
    @Size(max = 64)
    @JsonProperty("motherFirstNameEn")
    private String firstNameEn;
    @Size(max = 64)
    @JsonProperty("motherFirstNameMl")
    private String firstNameMl;
    @Size(max = 64)
    @JsonProperty("motherAadhar")
    private String motherAadhar;
    @Size(max = 64)
    @JsonProperty("motherMarriageAge")
    private Integer motherAgeMarriage;
    @Size(max = 64)
    @JsonProperty("motherMarriageBirth")
    private Integer motherAgeDelivery;
    @Size(max = 64)
    @JsonProperty("motherEducation")
    private String motherEducationid;
    @Size(max = 64)
    @JsonProperty("motherProfession")
    private String motherProffessionid;
    @Size(max = 64)
    @JsonProperty("motherNationality")
    private String motherNationalityid;
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
    @Size(max = 64)
    @JsonProperty("fatherEducation")
    private String fatherEucationid;
    @Size(max = 64)
    @JsonProperty("fatherProfession")
    private String fatherProffessionid;
    @Size(max = 64)
    @JsonProperty("Religion")
    private String religionId;
    @Size(max = 64)
    @JsonProperty("fatherMobile")
    private String familyMobileNo;
    @Size(max = 64)
    @JsonProperty("fatherEmail")
    private String familyEmailid;
    @Size(max = 64)
    @JsonProperty("fatherPassport")
    private String fatherPassport;
    @Size(max = 64)
    @JsonProperty("motherPassport")
    private String motherPassport;
    @Size(max = 64)
    @JsonProperty("motherBioAdopt")
    private String motherBioAdopt;

    @Size(max = 64)
    @JsonProperty("fatherBioAdopt")
    private String fatherBioAdopt;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
