package org.egov.ptr.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.Getter;

@ApiModel(description = "Details of the owner of the pet")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2022-08-16T15:34:24.436+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Owner {
	@JsonProperty("id")
	@Builder.Default
	private Long id = null;

	@JsonProperty("uuid")
	@Builder.Default
	private String uuid = null;

	@JsonProperty("userName")
	@Builder.Default
	private String userName = null;

	@JsonProperty("password")
	@Builder.Default
	private String password = null;

	@JsonProperty("salutation")
	@Builder.Default
	private String salutation = null;

	@JsonProperty("name")
	@Builder.Default
	private String name = null;

	@JsonProperty("gender")
	@Builder.Default
	private String gender = null;

	@JsonProperty("mobileNumber")
	@Builder.Default
	private String mobileNumber = null;

	@JsonProperty("emailId")
	@Builder.Default
	private String emailId = null;

	@JsonProperty("altContactNumber")
	@Builder.Default
	private String altContactNumber = null;

	@JsonProperty("pan")
	@Builder.Default
	private String pan = null;

	@JsonProperty("aadhaarNumber")
	@Builder.Default
	private String aadhaarNumber = null;

	@JsonProperty("permanentAddress")
	@Builder.Default
	private String permanentAddress = null;

	@JsonProperty("permanentCity")
	@Builder.Default
	private String permanentCity = null;

	@JsonProperty("permanentPincode")
	@Builder.Default
	private String permanentPincode = null;

	@JsonProperty("correspondenceCity")
	@Builder.Default
	private String correspondenceCity = null;

	@JsonProperty("correspondencePincode")
	@Builder.Default
	private String correspondencePincode = null;

	@JsonProperty("correspondenceAddress")
	@Builder.Default
	private String correspondenceAddress = null;

	@JsonProperty("active")
	@Builder.Default
	private Boolean active = null;

	@JsonProperty("dob")
	@Builder.Default
	private Long dob = null;

	@JsonProperty("pwdExpiryDate")
	@Builder.Default
	private Long pwdExpiryDate = null;

	@JsonProperty("locale")
	@Builder.Default
	private String locale = null;

	@JsonProperty("type")
	@Builder.Default
	private String type = null;

	@JsonProperty("signature")
	@Builder.Default
	private String signature = null;

	@JsonProperty("accountLocked")
	@Builder.Default
	private Boolean accountLocked = null;

	@JsonProperty("roles")
	@Valid
	@Builder.Default
	private List<org.egov.common.contract.request.Role> roles = null;

	@JsonProperty("fatherOrHusbandName")
	@Builder.Default
	private String fatherOrHusbandName = null;

	@JsonProperty("bloodGroup")
	@Builder.Default
	private String bloodGroup = null;

	@JsonProperty("identificationMark")
	@Builder.Default
	private String identificationMark = null;

	@JsonProperty("photo")
	@Builder.Default
	private String photo = null;

	@JsonProperty("createdBy")
	@Builder.Default
	private String createdBy = null;

	@JsonProperty("createdDate")
	@Builder.Default
	private Long createdDate = null;

	@JsonProperty("lastModifiedBy")
	@Builder.Default
	private String lastModifiedBy = null;

	@JsonProperty("lastModifiedDate")
	@Builder.Default
	private Long lastModifiedDate = null;

	@JsonProperty("otpReference")
	@Builder.Default
	private String otpReference = null;

	@JsonProperty("tenantId")
	@Builder.Default
	private String tenantId = null;

	// Owner-specific fields
	@JsonProperty("isPrimaryOwner")
	@Builder.Default
	private Boolean isPrimaryOwner = null;

	@JsonProperty("ownerType")
	@Builder.Default
	private String ownerType = null;

	@JsonProperty("ownershipPercentage")
	@Builder.Default
	private String ownershipPercentage = null;

	@JsonProperty("institutionId")
	@Builder.Default
	private String institutionId = null;

	@JsonProperty("relationship")
	@Builder.Default
	private String relationship = null;

	@JsonProperty("status")
	@Builder.Default
	private String status = null;

	public Owner addRolesItem(org.egov.common.contract.request.Role rolesItem) {
		if (this.roles == null) {
			this.roles = new ArrayList<>();
		}
		this.roles.add(rolesItem);
		return this;
	}

}
