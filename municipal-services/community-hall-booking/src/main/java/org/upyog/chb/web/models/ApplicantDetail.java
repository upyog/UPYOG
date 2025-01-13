package org.upyog.chb.web.models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Details of the community halls booking
 */
@ApiModel(description = "Details of the community halls booking")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApplicantDetail   {
	
	private String applicantDetailId;
	
	private String bookingId;
	
	@NotBlank(message = "CHB_BLANK_APPLICANT_NAME")
	@Size(max = 100, message = "COMMON_MAX_VALIDATION")
	private String applicantName;
	
	@NotBlank
	@Size(min = 10, max = 10)
	private String applicantMobileNo;
	
	private String applicantAlternateMobileNo;
	
	@NotBlank
	@Email
	private String applicantEmailId;
	
    @NotBlank
    @Size(min = 8, max = 18)
    private String accountNumber;

    @NotBlank
    private String ifscCode;

    @NotBlank
    private String bankName;

    @NotBlank
    private String bankBranchName;

    @NotBlank
    private String accountHolderName;
    
    private String refundType;
    
    private String refundStatus;
    
    private AuditDetails auditDetails;
    
    
    @JsonProperty("additionalDetails")
	private Object additionalDetails;

    
}

