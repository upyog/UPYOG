package org.egov.garbageservice.model.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * User
 */
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

	@JsonProperty("id")
	private Long id;

	@Size(max = 64)
	@JsonProperty("uuid")
	private String uuid;

	@Size(max = 64)
	@JsonProperty("userName")
	private String userName;

	@Size(max = 64)
	@JsonProperty("password")
	private String password;

	@JsonProperty("salutation")
	private String salutation;

	@NotNull
	@Size(max = 100)
	// @Pattern(regexp = "^[^\\$\"'<>?\\\\~`!@#$%^()+={}\\[\\]*,:;“”‘’]*$", message
	// = "Invalid name. Only alphabets and special characters -, ',`, .")
	@JsonProperty("name")
	private String name;

	@JsonProperty("gender")
	private String gender;

	// @Pattern(regexp = "(^[6-9][0-9]{9}$)", message = "Inavlid mobile number,
	// should start with 6-9 and contain ten digits of 0-9")
	@NotNull
	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@Size(max = 128)
	@JsonProperty("emailId")
	private String emailId;

	@Size(max = 50)
	@JsonProperty("altContactNumber")
	private String altContactNumber;

	@Size(max = 10)
	@JsonProperty("pan")
	private String pan;

	@Pattern(regexp = "^[0-9]{12}$", message = "AdharNumber should be 12 digit number")
	@JsonProperty("aadhaarNumber")
	private String aadhaarNumber;

	@Size(max = 300)
	@JsonProperty("permanentAddress")
	private String permanentAddress;

	@Size(max = 300)
	@JsonProperty("permanentCity")
	private String permanentCity;

	@Size(max = 10)
	@JsonProperty("permanentPinCode")
	private String permanentPincode;

	@Size(max = 300)
	@JsonProperty("correspondenceCity")
	private String correspondenceCity;

	@Size(max = 10)
	@JsonProperty("correspondencePinCode")
	private String correspondencePincode;

	@Size(max = 300)
	@JsonProperty("correspondenceAddress")
	private String correspondenceAddress;

	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("dob")
	private Long dob;

	@JsonProperty("pwdExpiryDate")
	private Long pwdExpiryDate;

	@Size(max = 16)
	@JsonProperty("locale")
	private String locale;

	@Size(max = 50)
	@JsonProperty("type")
	private String type;

	@Size(max = 36)
	@JsonProperty("signature")
	private String signature;

	@JsonProperty("accountLocked")
	private Boolean accountLocked;

	@JsonProperty("roles")
	@Valid
	private List<Role> roles;

	@Size(max = 100)
	@JsonProperty("fatherOrHusbandName")
	private String fatherOrHusbandName;

	@Size(max = 32)
	@JsonProperty("bloodGroup")
	private String bloodGroup;

	@Size(max = 300)
	@JsonProperty("identificationMark")
	private String identificationMark;

	@Size(max = 36)
	@JsonProperty("photo")
	private String photo;

	@Size(max = 64)
	@DiffIgnore
	@JsonProperty("createdBy")
	private String createdBy;

	@DiffIgnore
	@JsonProperty("createdDate")
	private Long createdDate;

	@Size(max = 64)
	@DiffIgnore
	@JsonProperty("lastModifiedBy")
	private String lastModifiedBy;

	@DiffIgnore
	@JsonProperty("lastModifiedDate")
	private Long lastModifiedDate;

	@Size(max = 256)
	@JsonProperty("tenantId")
	private String tenantId;

	@Size(max = 50)
	@JsonProperty("alternatemobilenumber")
	private String alternatemobilenumber;

	public User addRolesItem(Role rolesItem) {
		if (this.roles == null) {
			this.roles = new ArrayList<>();
		}
		this.roles.add(rolesItem);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return Objects.equals(uuid, user.uuid) && Objects.equals(name, user.name)
				&& Objects.equals(mobileNumber, user.mobileNumber);
	}

	@Override
	public int hashCode() {

		return Objects.hash(uuid, name, mobileNumber);
	}
}
