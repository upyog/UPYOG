package org.egov.ewst.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.egov.ewst.models.Role;
import org.egov.ewst.models.User;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@ApiModel(description = "Details of the user applying for ewaste application")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2022-08-16T15:34:24.436+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Applicant {
	@JsonProperty("id")
	private String id = null;

	@JsonProperty("applicantName")
	private String applicantName = null;

	@JsonProperty("gender")
	private String gender = null;

	@JsonProperty("mobileNumber")
	private String mobileNumber = null;

	@JsonProperty("emailId")
	private String emailId = null;

	@JsonProperty("altMobileNumber")
	private String altMobileNumber = null;

	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("ewId")
	private String ewId = null;
}