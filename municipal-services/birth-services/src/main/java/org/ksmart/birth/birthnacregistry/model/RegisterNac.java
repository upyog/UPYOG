package org.ksmart.birth.birthnacregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.common.model.AuditDetails;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RegisterNac {

	    @Size(max = 64)
	    @JsonProperty("id")
	    private String id;
	    
	    @Size(max = 64)
	    @JsonProperty("registrationno")
	    private String registrationno;
	    
//	    @Size(max = 64)
//	    @JsonProperty("RegistrationDate")
//	    private Long RegistrationDate;
	    
	    
	    @JsonProperty("dobStr")
	    private String dobStr;
	    
	    @JsonProperty("registration_date_str")
	    private String registrationDateStr;
	    
	    
//	    @Size(max = 64)
//	    @JsonProperty("RegistrationStatus")
//	    private String RegistrationStatus;
	    
	    @Size(max = 64)
	    @JsonProperty("birthdetailsid")
	    private String birthdetailsid;
	    
	    @Size(max = 64)
	    @JsonProperty("applicantnameen")
	    private String applicantnameen;
	    
	    @Size(max = 64)
	    @JsonProperty("careofapplicantnameen")
	    private String careofapplicantnameen;
	    
	    @Size(max = 64)
	    @JsonProperty("childnameen")
	    private String childnameen;
	    
	    @Size(max = 64)
	    @JsonProperty("dateofbirth")
	    private Long dateofbirth;
	    
	    @Size(max = 64)
	    @JsonProperty("mothernameen")
	    private String mothernameen;
	    
	    @Size(max = 64)
	    @JsonProperty("fathernameen")
	    private String fathernameen;
	    
	    
	    @Size(max = 64)
	    @JsonProperty("birthplaceen")
	    private String birthplaceen;
	    
	    
	    @Size(max = 64)
	    @JsonProperty("PlaceOfBirthId")
	    private String PlaceOfBirthId;
	    
	    @Size(max = 64)
	    @JsonProperty("HospitalId")
	    private String HospitalId;
	    
	    @Size(max = 64)
	    @JsonProperty("InstitutionId")
	    private String InstitutionId;
	    
	    
	    @Size(max = 64)
	    @JsonProperty("birthdistrictid")
	    private String birthdistrictid;
	    
	    @Size(max = 64)
	    @JsonProperty("birthstateid")
	    private String birthstateid;
	    
	    @Size(max = 64)
	    @JsonProperty("birthvillageid")
	    private String birthvillageid;

	   @Size(max = 64)
	   @JsonProperty("birthtalukid")
	   private String birthtalukid;

	    @Size(max = 64)
	    @JsonProperty("applicationtype")
	    private String applicationtype;
	    
	    @Size(max = 64)
	    @JsonProperty("filestoreid")
	    private String filestoreid;
	    
	    @Size(max = 64)
	    @JsonProperty("status")
	    private String status;
	    
	    @Size(max = 64)
	    @JsonProperty("additionaldetail")
	    private String additionaldetail;
	    
	    @Size(max = 64)
	    @JsonProperty("embeddedurl")
	    private String embeddedurl;
	    
	    @Size(max = 64)
	    @JsonProperty("dateofissue")
	    private Long dateofissue;
	    
	    @Size(max = 64)
	    @JsonProperty("tenantid")
	    private String tenantid;
	    
	    @Size(max = 64)
	    @JsonProperty("certificateno")
	    private String certificateno;
	    
	    @Size(max = 64)
	    @JsonProperty("ack_no")
	    private String ackNumber;
	    
	    @JsonProperty("dateofreport")
	    private Long dateOfReport;
	    
	    @Size(max = 64)
	    @JsonProperty("registration_status")
	    private String registrationStatus;

	    @JsonProperty("registration_date")
	    private Long registrationDate;

	    @JsonProperty("period")
	    private String period;
	   
	    
	    @Size(max = 2500)
	    @JsonProperty("permenantAddDetails")
	    private String permenantAddDetails;

	   
	    @JsonProperty("isBirthNAC")
	    private Boolean isBirthNAC ;

	    @JsonProperty("isBirthNIA")
	    private Boolean isBirthNIA ;
	    
	    
	    @JsonProperty("auditDetails")
	    private AuditDetails auditDetails;
	    /// for PDF SERVICE
	    private String fullName;
	    private String fullNameMl;
	    private String embeddedUrl;
	    private String dtbirth;
	    private String regno;
	    private String dateofreport;
	    private Long dtissue;
	    private Long remarks;
	    private String certId;
	    private String placeofbirth;
	    private String certificateType;
	    private String tenantTaluk;
	    private String tenantDistrict;
	    private String tenantLbType;
	    private String tenantState;
}
