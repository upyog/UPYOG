package org.ksmart.birth.birthnacregistry.model;

import javax.validation.constraints.Size;

 
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	    @JsonProperty("dateofbirthWord")
	    private String dobStrWord;

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
	    @JsonProperty("registar")
	    private String registarDetails;

	    

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
	    @JsonProperty("applicationId")
	    private String applicationId;

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
	    
	    @JsonProperty("currentTime")
	    private String currentTime;

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

	    @Size(max = 45)
	    @JsonProperty("careofapplicantnameen")
	    private String careofapplicantnameen;
	    
	    @Size(max = 45)
	    @JsonProperty("applicantnameen")
	    private String applicantnameen;
	    
	    @Size(max = 45)
	    @JsonProperty("birthplaceen")
	    private String birthplaceen;
	    
	    @Size(max = 45)
	    @JsonProperty("childnameen")
	    private String childnameen;
	    
	    @Size(max = 45)
	    @JsonProperty("mothernameen")
	    private String mothernameen;

	    @Size(max = 45)
	    @JsonProperty("period")
	    private String period;
	    
	    @Size(max = 45)
	    @JsonProperty("fathernameen")
	    private String fathernameen;
	    
	    @Size(max = 45)
	    @JsonProperty("permAddress")
	    private String permAddress;
	    
	    @JsonProperty("isBirthNAC")
	    private Boolean isBirthNAC;

	    @JsonProperty("isBirthNIA")
	    private Boolean isBirthNIA;
	    
	    
	    
	    
}
