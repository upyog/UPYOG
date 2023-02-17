package org.ksmart.birth.birthregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Size;

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

    @JsonProperty("dateofbirthstr")
    private String dobStr;

    @JsonProperty("registrationDateStr")
    private String registrationDateStr;

    @JsonProperty("dateofbirth")
    private Long dateOfBirth;

    @Size(max = 1000)
    @JsonProperty("firstname_en")
    private String firstNameEn;

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
    @JsonProperty("placeDetails")
    private String placeDetails;

    @Size(max = 2500)
    @JsonProperty("placeDetailsMl")
    private String placeDetailsMl;

//    @Size(max = 64)
//    @JsonProperty("placeofbirthid")
//    private String placeOfBirthId;
//
//    @Size(max = 2500)
//    @JsonProperty("hospitalDetails")
//    private String hospitalDetails;
//
//    @Size(max = 2500)
//    @JsonProperty("institutionDetails")
//    private String institutionDetails;
//
//    @Size(max = 2500)
//    @JsonProperty("homeDetails")
//    private String homeDetails;
//
//    @Size(max = 2500)
//    @JsonProperty("vehicleDetails")
//    private String vehicleDetails;
//
//    @Size(max = 2500)
//    @JsonProperty("publicDetails")
//    private String publicDetails;

//    @Size(max = 2500)
//    @JsonProperty("hospitalDetailsMl")
//    private String hospitalDetailsMl;
//
//    @Size(max = 2500)
//    @JsonProperty("institutionDetailsMl")
//    private String institutionDetailsMl;
//
//    @Size(max = 2500)
//    @JsonProperty("homeDetailsMl")
//    private String homeDetailsMl;
//
//    @Size(max = 2500)
//    @JsonProperty("vehicleDetailsMl")
//    private String vehicleDetailsMl;
//
//    @Size(max = 2500)
//    @JsonProperty("publicDetailsMl")
//    private String publicDetailsMl;

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
    @JsonProperty("tenantDistrict")
    private String tenantDistrict;

    @Size(max = 1000)
    @JsonProperty("tenantTaluk")
    private String tenantTaluk;

    @Size(max = 1000)
    @JsonProperty("tenantState")
    private String tenantState;
}
