package org.egov.vehicle.web.model.user;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.egov.common.contract.request.Role;
import org.egov.vehicle.validation.SanitizeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@JsonProperty("id")
	private String id;


	@SanitizeHtml
	@Size(max = 64)
	@JsonProperty("uuid")
	private String uuid;

	@Size(max = 64)
	@SanitizeHtml
	@JsonProperty("userName")
	private String userName;

	@Size(max = 64)
	@SanitizeHtml
	@JsonProperty("password")
	private String password;

	@SanitizeHtml
	@JsonProperty("salutation")
	private String salutation;

	@NotNull
	@SanitizeHtml
	@Size(max = 100)
	@Pattern(regexp = "^[a-zA-Z0-9 \\-'`\\.]*$", message = "Invalid name. Only alphabets and special characters -, ',`, .")
	@JsonProperty("name")
	private String name;

	@NotNull
	@SanitizeHtml
	@JsonProperty("gender")
	private String gender;

	@NotNull
	@SanitizeHtml
	@Pattern(regexp = "^[0-9]{10}$", message = "MobileNumber should be 10 digit number")
	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@Size(max = 128)
	@Email (message = "EmailId should be in proper format")
	// @NotNull
	@JsonProperty("emailId")
	private String emailId;

	@Size(max = 50)
	@SanitizeHtml
	@JsonProperty("altContactNumber")
	private String altContactNumber;

	@Size(max = 10)
	@SanitizeHtml
	@JsonProperty("pan")
	private String pan;

	@Pattern(regexp = "^[0-9]{12}$", message = "AdharNumber should be 12 digit number")
	@SanitizeHtml
	@JsonProperty("aadhaarNumber")
	private String aadhaarNumber;

	@Size(max = 300)
	@SanitizeHtml
	@JsonProperty("permanentAddress")
	private String permanentAddress;

	@Size(max = 300)
	@JsonProperty("permanentCity")
	private String permanentCity;

	@Size(max = 10)
	@SanitizeHtml
	@JsonProperty("permanentPinCode")
	private String permanentPincode;

	@Size(max = 300)
	@SanitizeHtml
	@JsonProperty("correspondenceCity")
	private String correspondenceCity;

	@Size(max = 10)
	@SanitizeHtml
	@JsonProperty("correspondencePinCode")
	private String correspondencePincode;

	@Size(max = 300)
	@SanitizeHtml
	@JsonProperty("correspondenceAddress")
	private String correspondenceAddress;

	@JsonProperty("active")
	private Boolean active;

	// @NotNull
	@JsonProperty("dob")
	private Long dob;

	@JsonProperty("pwdExpiryDate")
	private Long pwdExpiryDate;

	@Size(max = 16)
	@SanitizeHtml
	@JsonProperty("locale")
	private String locale;

	@Size(max = 50)
	@SanitizeHtml
	@JsonProperty("type")
	private String type;

	@Size(max = 36)
	@SanitizeHtml
	@JsonProperty("signature")
	private String signature;

	@JsonProperty("accountLocked")
	private Boolean accountLocked;

	@JsonProperty("roles")
	@Valid
	private List<Role> roles;

	// @NotNull
	@SanitizeHtml
	@Size(max = 100)
	@JsonProperty("fatherOrHusbandName")
	private String fatherOrHusbandName;

	// @NotNull
	@JsonProperty("relationship")
	private GuardianRelation relationship;

	public enum GuardianRelation {
		FATHER, MOTHER, HUSBAND, OTHER;
	}

	@Size(max = 32)
	@SanitizeHtml
	@JsonProperty("bloodGroup")
	private String bloodGroup;

	@Size(max = 300)
	@SanitizeHtml
	@JsonProperty("identificationMark")
	private String identificationMark;

	@Size(max = 36)
	@JsonProperty("photo")
	private String photo;

	@Size(max = 64)
	@SanitizeHtml
	@JsonProperty("createdBy")
	private String createdBy;

	@JsonProperty("createdDate")
	private Long createdDate;

	@Size(max = 64)
	@SanitizeHtml
	@JsonProperty("lastModifiedBy")
	private String lastModifiedBy;

	@JsonProperty("lastModifiedDate")
	private Long lastModifiedDate;

	@SanitizeHtml
	@JsonProperty("otpReference")
	private String otpReference;

	@Size(max = 256)
	@SanitizeHtml
	@NonNull
	@JsonProperty("tenantId")
	private String tenantId;
}
