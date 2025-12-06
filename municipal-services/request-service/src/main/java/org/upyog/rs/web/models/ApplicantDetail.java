package org.upyog.rs.web.models;

<<<<<<< HEAD
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
=======
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
>>>>>>> master-LTS

import org.springframework.validation.annotation.Validated;
import org.upyog.rs.validator.CreateApplicationGroup;

<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Details of the advertisement booking
 */
<<<<<<< HEAD
@Schema(description = "Details of the advertisement booking")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
=======
@ApiModel(description = "Details of the advertisement booking")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApplicantDetail   {

	private String applicantId;

	private String bookingId;

	@NotBlank
	@Size(max = 100, message = "COMMON_MAX_VALIDATION")
	private String name;

	@NotBlank
	@Size(min = 10, max = 10)
	private String mobileNumber;

	private String alternateNumber;

	@NotBlank
	@Email
	@Size(min = 5, max = 200, message = "Email ID must be between 5 and 200 characters")
	private String emailId;

	private String gender;

	private AuditDetails auditDetails;
	
}

