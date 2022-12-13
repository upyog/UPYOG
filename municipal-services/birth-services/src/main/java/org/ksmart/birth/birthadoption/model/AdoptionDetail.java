package org.ksmart.birth.birthadoption.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ksmart.birth.birthadoption.model.adoption.AdoptionFatherInfo;
import org.ksmart.birth.birthadoption.model.adoption.AdoptionMotherInfo;
import org.ksmart.birth.birthadoption.model.adoption.AdoptionPermanentAddress;
import org.ksmart.birth.birthadoption.model.adoption.AdoptionPresentAddress;
import org.ksmart.birth.birthadoption.model.birth.*;
import org.ksmart.birth.common.model.AuditDetails;

import javax.validation.constraints.Size;

public class AdoptionDetail {
    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @JsonProperty("dateofreport")
    private Long dateOfReport;

    @JsonProperty("dateofbirth")
    private Long dateOfBirth;

    @JsonProperty("timeofbirth")
    private Long timeOfBirth;

    @Size(max = 20)
    @JsonProperty("am_pm")
    private String ampm;

    @Size(max = 1000)
    @JsonProperty("firstname_en")
    private String firstNameEn;

    @Size(max = 1000)
    @JsonProperty("firstname_ml")
    private String firstNameMl;

    @Size(max = 1000)
    @JsonProperty("middlename_en")
    private String middleNameEn;

    @Size(max = 1000)
    @JsonProperty("middlename_ml")
    private String middleNameMl;

    @Size(max = 1000)
    @JsonProperty("lastname_en")
    private String lastNameEn;

    @Size(max = 1000)
    @JsonProperty("lastname_ml")
    private String lastNameMl;

    @Size(max = 64)
    @JsonProperty("tenantid")
    private String tenantId;

    @JsonProperty("gender")
    private Integer gender;

    @Size(max = 2500)
    @JsonProperty("remarks_en")
    private String remarksEn;

    @Size(max = 2500)
    @JsonProperty("remarks_ml")
    private String remarksMl;

    @Size(max = 15)
    @JsonProperty("aadharno")
    private String aadharNo;

    @Size(max = 64)
    @JsonProperty("esign_user_code")
    private String esignUserCode;

    @Size(max = 64)
    @JsonProperty("esign_user_desig_code")
    private String esignUserDesigCode;

    @JsonProperty("is_adopted")
    private Boolean isAdopted;

    @JsonProperty("is_abandoned")
    private Boolean isAbandoned;

    @JsonProperty("is_multiple_birth")
    private Boolean isMultipleBirth;

    @JsonProperty("is_father_info_missing")
    private Boolean isFatherInfoMissing;

    @JsonProperty("is_mother_info_missing")
    private Boolean isMotherInfoMissing;

    @JsonProperty("no_of_alive_birth")
    private Integer noOfAliveBirth;

    @Size(max = 64)
    @JsonProperty("multiplebirthdetid")
    private String multipleBirthDeeailsIid;

    @JsonProperty("is_born_outside")
    private Boolean isBornOutside;

    @Size(max = 64)
    @JsonProperty("ot_passportno")
    private String passportNo;

    @JsonProperty("ot_dateofarrival")
    private Long dateOfArrival;

    @Size(max = 64)
    @JsonProperty("applicationtype")
    private String applicationType;

    @Size(max = 64)
    @JsonProperty("businessservice")
    private String businessService;

    @Size(max = 64)
    @JsonProperty("workflowcode")
    private String workFlowCode;

    @Size(max = 64)
    @JsonProperty("fm_fileno")
    private String fmFileNo;

    @JsonProperty("file_date")
    private Long fileDate;

    @Size(max = 64)
    @JsonProperty("applicationno")
    private String applicationNo;

    @Size(max = 64)
    @JsonProperty("registrationno")
    private String registrationNo;

    @JsonProperty("registration_date")
    private Long registrationDate;

    @Size(max = 64)
    @JsonProperty("action")
    private String action;

    @Size(max = 64)
    @JsonProperty("status")
    private String status;

    @Size(max = 1000)
    @JsonProperty("a_firstname_en")
    private String adoptFirstNameEn;

    @Size(max = 1000)
    @JsonProperty("adopt_firstname_ml")
    private String adoptFirstNameMl;

    @Size(max = 1000)
    @JsonProperty("adopt_middlename_en")
    private String adoptMiddleNameEn;

    @Size(max = 1000)
    @JsonProperty("adopt_middlename_ml")
    private String adoptMiddleNameMl;

    @Size(max = 1000)
    @JsonProperty("adopt_lastname_en")
    private String adoptLastNameEn;

    @Size(max = 1000)
    @JsonProperty("adopt_lastname_ml")
    private String adoptLastNameMl;

    @Size(max = 64)
    @JsonProperty("adopt_deed_order_no")
    private String adoptDeedOrderNo;

    @JsonProperty("adopt_dateoforder_deed")
    private Long adoptDateOfOrderDeed;

    @Size(max = 1000)
    @JsonProperty("adopt_issuing_auththority")
    private String adoptIssuingAuththority;

    @JsonProperty("adopt_has_agency")
    private Boolean adoptHasAgency;

    @JsonProperty("adopt_agency_name")
    private String adoptAgencyName;

    @JsonProperty("adopt_agency_address")
    private String adoptAgencyAddress;


    @JsonProperty("birthDetails")
    private AdoptionDetail birthDetails;

    @JsonProperty("birthPlace")
    private BirthPlace birthPlace;

    @JsonProperty("birthFather")
    private BirthFatherInfo birthFatherInfo;

    @JsonProperty("birthMother")
    private BirthMotherInfo birthMotherInfo;

    @JsonProperty("birthPermanent")
    private BirthPermanentAddress birthPermanentAddress;

    @JsonProperty("birthPresent")
    private BirthPresentAddress birthPresentAddress;

    @JsonProperty("birthStatistical")
    private BirthStatisticalInformation birthStatisticalInformation;

    @JsonProperty("adoptionMother")
    private AdoptionMotherInfo adoptionMotherInfo;

    @JsonProperty("adoptionFather")
    private AdoptionFatherInfo adoptionFatherInfo;

    @JsonProperty("adoptionPermanentAddress")
    private AdoptionPermanentAddress adoptionPermanentAddress;

    @JsonProperty("adoptionPresentAddress")
    private AdoptionPresentAddress adoptionPresentAddress;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
