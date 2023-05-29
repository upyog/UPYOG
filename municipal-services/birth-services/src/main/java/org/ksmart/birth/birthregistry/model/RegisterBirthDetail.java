package org.ksmart.birth.birthregistry.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.common.model.AuditDetails;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RegisterBirthDetail {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @JsonProperty("dateofreport")
    private Long dateOfReport;

    @JsonProperty("dateofbirthstr")
    private String dobStr;

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

    @Size(max = 1000)
    @JsonProperty("gender")
    private String gender;

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
    private String multipleBirthDetId;

    @JsonProperty("is_born_outside")
    private Boolean isBornOutside;

    @Size(max = 100)
    @JsonProperty("ot_passportno")
    private String otPassportNo;

    @Size(max = 128)
    @JsonProperty("registration_no")
    private String registrationNo;

    @Size(max = 64)
    @JsonProperty("registration_status")
    private String registrationStatus;

    @JsonProperty("registration_date")
    private Long registrationDate;

    @JsonProperty("registration_date_str")
    private String registrationDateStr;

    @Size(max = 64)
    @JsonProperty("ack_no")
    private String ackNumber;

    @Size(max = 64)
    @JsonProperty("applicationId")
    private String applicationId;

    @Size(max = 64)
    @JsonProperty("applicationType")
    private String applicationType;

    @JsonProperty("childArrivalDate")
    private Long childArrivalDate;

    @JsonProperty("registerBirthPlace")
    private RegisterBirthPlace registerBirthPlace;

    @JsonProperty("registerBirthFather")
    private RegisterBirthFatherInfo registerBirthFather;

    @JsonProperty("registerBirthMother")
    private RegisterBirthMotherInfo registerBirthMother;

    @JsonProperty("registerBirthPermanent")
    private RegisterBirthPermanentAddress registerBirthPermanent;

    @JsonProperty("registerBirthPresent")
    private RegisterBirthPresentAddress registerBirthPresent;

    @JsonProperty("registerBirthStatitical")
    private RegisterBirthStatiticalInformation registerBirthStatitical;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("isMigrated")
    private Boolean isMigrated;

    @Size(max = 10)
    @JsonProperty("migratedFrom")
    private String migratedFrom;

    /// for PDF SERVICE
    private String fullName;
    private String fullNameMl;
    private String embeddedUrl;
    private String dateofbirth;
    private String dateofreport;
    private Long dateofissue;
    private Long remarks;
    private String certId;
    private String placeofbirth;
    private String certificateType;
    private String tenantTaluk;
    private String tenantDistrict;
    private String tenantLbType;
    private String tenantState;



}
