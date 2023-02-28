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

    @JsonProperty("dateofreportStr")
    private String dateOfReportStr;

    @JsonProperty("dateofbirthstr")
    private String dobStr;

    @JsonProperty("registrationDateStr")
    private String registrationDateStr;

    @JsonProperty("dateofbirth")
    private Long dateOfBirth;

    @Size(max = 1000)
    @JsonProperty("fullName")
    private String fullName;
    @Size(max = 1000)
    @JsonProperty("fullNameMl")
    private String fullNameMl;

    @Size(max = 64)
    @JsonProperty("ackNo")
    private String ackNo;
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
    @JsonProperty("placeDetails")
    private String placeDetails;

    @Size(max = 64)
    @JsonProperty("registrationno")
    private String registrationNo;

    @Size(max = 64)
    @JsonProperty("certId")
    private String certId;
    @Size(max = 2500)
    @JsonProperty("embeddedUrl")
    private String embeddedUrl;
    @JsonProperty("registrationDate")
    private Long registrationDate;

    @JsonProperty("currentDate")
    private String currentDate;

    @Size(max = 2500)
    @JsonProperty("placeDetailsMl")
    private String placeDetailsMl;

    @Size(max = 2500)
    @JsonProperty("motherDetails")
    private String motherDetails;

    @Size(max = 2500)
    @JsonProperty("motherDetailsMl")
    private String motherDetailsMl;

    @Size(max = 2500)
    @JsonProperty("fatherDetails")
    private String fatherDetails;

    @Size(max = 2500)
    @JsonProperty("fatherDetailsMl")
    private String fatherDetailsMl;

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

    @Size(max = 64)
    @JsonProperty("perCountry")
    private String perCountry;

    @Size(max = 64)
    @JsonProperty("perState")
    private String perState;

    @Size(max = 64)
    @JsonProperty("perDistrict")
    private String perDistrict;

    @Size(max = 64)
    @JsonProperty("perPostOffice")
    private String perPostOffice;
    @Size(max = 1000)
    @JsonProperty("perLocalityEn")
    private String perLocalityEn;

    @Size(max = 1000)
    @JsonProperty("perLocalityMl")
    private String perLocalityMl;


    @Size(max = 1000)
    @JsonProperty("perStreetEn")
    private String perStreetEn;

    @Size(max = 1000)
    @JsonProperty("perStreetMl")
    private String perStreetMl;

    @Size(max = 1000)
    @JsonProperty("perHouseEn")
    private String perHouseEn;

    @Size(max = 1000)
    @JsonProperty("perHouseMl")
    private String perHouseMl;
    @Size(max = 64)
    @JsonProperty("presCountry")
    private String presCountry;

    @Size(max = 64)
    @JsonProperty("presState")
    private String presState;

    @Size(max = 64)
    @JsonProperty("presDistrict")
    private String presDistrict;

    @Size(max = 64)
    @JsonProperty("presPostOffice")
    private String presPostOffice;
    @Size(max = 1000)
    @JsonProperty("presLocalityEn")
    private String presLocalityEn;

    @Size(max = 1000)
    @JsonProperty("presLocalityMl")
    private String presLocalityMl;


    @Size(max = 1000)
    @JsonProperty("presStreetEn")
    private String presStreetEn;

    @Size(max = 1000)
    @JsonProperty("presStreetMl")
    private String presStreetMl;

    @Size(max = 1000)
    @JsonProperty("presHouseEn")
    private String presHouseEn;

    @Size(max = 1000)
    @JsonProperty("presHouseMl")
    private String presHouseMl;


    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("offset")
    private Integer offset;


}
