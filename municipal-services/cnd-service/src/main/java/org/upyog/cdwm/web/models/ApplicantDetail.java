package org.upyog.cdwm.web.models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;
import org.upyog.cdwm.validator.CreateApplicationGroup;

import digit.models.coremodels.AuditDetails;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Details of the advertisement booking
 */
@ApiModel(description = "Details of the applicant details")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApplicantDetail   {

	private String applicantId;

	private String bookingId;

	@NotBlank(groups = CreateApplicationGroup.class ,message = "RS_BLANK_APPLICANT_NAME")
	@Size(max = 100, message = "COMMON_MAX_VALIDATION")
	private String name;

	@NotBlank(groups = CreateApplicationGroup.class)
	@Size(min = 10, max = 10)
	private String mobileNumber;

	private String alternateNumber;

	@NotBlank(groups = CreateApplicationGroup.class)
	@Email
	private String emailId;

	private String gender;

	private AuditDetails auditDetails;
	
}

