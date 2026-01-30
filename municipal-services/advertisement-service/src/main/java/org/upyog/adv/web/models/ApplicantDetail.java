package org.upyog.adv.web.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import org.springframework.validation.annotation.Validated;
import org.upyog.adv.validator.CreateApplicationGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Details of the advertisement booking
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApplicantDetail   {
	
	private String applicantDetailId;
	
	private String bookingId;
	
	@NotBlank(groups = CreateApplicationGroup.class ,message = "ADV_BLANK_APPLICANT_NAME")
	@Size(max = 100, message = "COMMON_MAX_VALIDATION")
	private String applicantName;
	
	@NotBlank(groups = CreateApplicationGroup.class)
	@Size(min = 10, max = 10)
	private String applicantMobileNo;
	
	private String applicantAlternateMobileNo;
	
	@NotBlank(groups = CreateApplicationGroup.class)
	@Email
	private String applicantEmailId;
	
    private AuditDetails auditDetails;
    
}

