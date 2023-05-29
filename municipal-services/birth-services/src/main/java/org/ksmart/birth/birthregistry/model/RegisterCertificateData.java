package org.ksmart.birth.birthregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterCertificateData {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @JsonProperty("dateofreport")
    private Long dateOfReport;

//    @JsonProperty("dateofreportStr")
//    private String dateOfReportStr;
//
    @JsonProperty("dateofbirthstr")
    private String dobStr;

    @JsonProperty("dateofbirthWord")
    private String dobStrWord;

    @JsonProperty("registrationDate")
    private Long registrationDate;

    @JsonProperty("dateofbirth")
    private Long dateOfBirth;

    @Size(max = 1000)
    @JsonProperty("fullName")
    private String fullName;
    @Size(max = 1000)
    @JsonProperty("fullNameMl")
    private String fullNameMl;

    @Size(max = 64)
    @JsonProperty("childAadharNo")
    private String childAadharNo;

    @Size(max = 64)
    @JsonProperty("ackNo")
    private String ackNo;

    @Size(max = 64)
    @JsonProperty("keyNo")
    private String keyNo;

    @Size(max = 64)
    @JsonProperty("tenantid")
    private String tenantId;

    @Size(max = 1000)
    @JsonProperty("genderEn")
    private String genderEn;

    @Size(max = 1000)
    @JsonProperty("genderMl")
    private String genderMl;

    @Size(max = 2500)
    @JsonProperty("remarks_en")
    private String remarksEn;

    @Size(max = 2500)
    @JsonProperty("remarks_ml")
    private String remarksMl;

    @Size(max = 1000)
    @JsonProperty("registar")
    private String registarDetails;

    @JsonProperty("is_adopted")
    private Boolean isAdopted;

    @Size(max = 64)
    @JsonProperty("birthPlaceId")
    private String birthPlaceId;

    @Size(max = 64)
    @JsonProperty("birthPlaceHospitalId")
    private String birthPlaceHospitalId;

    @Size(max = 64)
    @JsonProperty("birthPlaceInstitutionlId")
    private String birthPlaceInstitutionId;

    @Size(max = 64)
    @JsonProperty("birthPlaceInstitutionlTypeId")
    private String birthPlaceInstitutionlTypeId;


    @Size(max = 64)
    @JsonProperty("placeDetails")
    private String placeDetails;

    @Size(max = 64)
    @JsonProperty("registrationno")
    private String registrationNo;

    @Size(max = 64)
    @JsonProperty("applicationId")
    private String applicationId;

    @Size(max = 64)
    @JsonProperty("certId")
    private String certId;
    @Size(max = 2500)
    @JsonProperty("embeddedUrl")
    private String embeddedUrl;

    @JsonProperty("currentDate")
    private String currentDate;

    @JsonProperty("currentDateLong")
    private Long currentDateLong;

    @JsonProperty("currentTime")
    private String currentTime;

    @JsonProperty("updatingDate")
    private Long updatingDate;

    @JsonProperty("updatingTime")
    private String updatingTime;

    @Size(max = 2500)
    @JsonProperty("placeDetailsMl")
    private String placeDetailsMl;

    @Size(max = 2500)
    @JsonProperty("motherDetails")
    private String motherDetails;

    @Size(max = 2500)
    @JsonProperty("motherDetailsMl")
    private String motherDetailsMl;

    @Size(max = 64)
    @JsonProperty("motherAadharNo")
    private String motherAadharNo;

    @Size(max = 2500)
    @JsonProperty("fatherDetails")
    private String fatherDetails;

    @Size(max = 2500)
    @JsonProperty("fatherDetailsMl")
    private String fatherDetailsMl;

    @Size(max = 64)
    @JsonProperty("fatherAadharNo")
    private String fatherAadharNo;

    @Size(max = 2500)
    @JsonProperty("permenantAddDetails")
    private String permenantAddDetails;

    @Size(max = 2500)
    @JsonProperty("permenantAddDetailsMl")
    private String permenantAddDetailsMl;

    @Size(max = 2500)
    @JsonProperty("presentAddDetails")
    private String presentAddDetails;

    @Size(max = 2500)
    @JsonProperty("presentAddDetailsMl")
    private String presentAddDetailsMl;

    @Size(max = 1000)
    @JsonProperty("tenantLbType")
    private String tenantLbType;

    @Size(max = 1000)
    @JsonProperty("tenantLbTypeMl")
    private String tenantLbTypeMl;

    @Size(max = 1000)
    @JsonProperty("tenantDistrict")
    private String tenantDistrict;

    @Size(max = 1000)
    @JsonProperty("tenantDistrictMl")
    private String tenantDistrictMl;

    @Size(max = 1000)
    @JsonProperty("tenantTaluk")
    private String tenantTaluk;

    @Size(max = 1000)
    @JsonProperty("tenantTalukMl")
    private String tenantTalukMl;

    @Size(max = 1000)
    @JsonProperty("tenantState")
    private String tenantState;

    @Size(max = 1000)
    @JsonProperty("tenantStateMl")
    private String tenantStateMl;
    @Size(max = 1000)
    @JsonProperty("wardCode")
    private String wardCode;

    @Size(max = 45)
    @JsonProperty("applicationType")
    private String applicationType;

    @Size(max = 1000)
    @JsonProperty("mainPlaceDetails")
    private String mainPlaceDetails;

    @Size(max = 1000)
    @JsonProperty("mainPlaceDetailsMl")
    private String mainPlaceDetailsMl;

    @JsonProperty("isMigrated")
    private Boolean isMigrated;


    @Size(max = 10)
    @JsonProperty("migratedFrom")
    private String migratedFrom;


}
