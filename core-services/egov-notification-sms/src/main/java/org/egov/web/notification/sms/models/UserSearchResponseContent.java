package org.egov.web.notification.sms.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.web.notification.sms.enums.GuardianRelation;
import org.egov.web.notification.sms.enums.UserType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponseContent {

	private Long id;
	private String userName;
	private String salutation;
	private String name;
	private String gender;
	private String mobileNumber;
	private String emailId;
	private String altContactNumber;
	private String pan;
	private String aadhaarNumber;
	private String permanentAddress;
	private String permanentCity;
	private String permanentPinCode;
	private String correspondenceAddress;
	private String correspondenceCity;
	private String correspondencePinCode;
	private String alternatemobilenumber;

	@JsonIgnore
	private Set<Address> addresses;

	private Boolean active;
	private String locale;
	private UserType type;
	private Boolean accountLocked;
	private Long accountLockedDate;
	private String fatherOrHusbandName;
	private GuardianRelation relationship;
	private String signature;
	private String bloodGroup;
	private String photo;
	private String identificationMark;
	private Long createdBy;
	private Long lastModifiedBy;
	private String tenantId;
	private Set<RoleRequest> roles;
	private String uuid;

	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private Date createdDate;
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private Date lastModifiedDate;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dob;
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private Date pwdExpiryDate;

	public UserSearchResponseContent(User user) {

		this.id = user.getId();
		this.userName = user.getUsername();
		this.salutation = user.getSalutation();
		this.name = user.getName();
		this.gender = user.getGender() != null ? user.getGender().toString() : null;
		this.mobileNumber = user.getMobileNumber();
		this.emailId = user.getEmailId();
		this.altContactNumber = user.getAltContactNumber();
		this.pan = user.getPan();
		this.aadhaarNumber = user.getAadhaarNumber();
		this.active = user.getActive();
		this.dob = user.getDob();
		this.pwdExpiryDate = user.getPasswordExpiryDate();
		this.locale = user.getLocale();
		this.type = user.getType();
		this.accountLocked = user.getAccountLocked();
		this.accountLockedDate = user.getAccountLockedDate();
		this.signature = user.getSignature();
		this.bloodGroup = user.getBloodGroup() != null ? user.getBloodGroup().getValue() : null;
		this.photo = user.getPhoto();
		this.identificationMark = user.getIdentificationMark();
		this.createdBy = user.getCreatedBy();
		this.createdDate = user.getCreatedDate();
		this.lastModifiedBy = user.getLastModifiedBy();
		this.lastModifiedDate = user.getLastModifiedDate();
		this.tenantId = user.getTenantId();
		this.roles = convertDomainRolesToContract(user.getRoles());
		this.fatherOrHusbandName = user.getGuardian();
		this.relationship = user.getGuardianRelation();
		this.uuid = user.getUuid();
		this.addresses = user.getAddresses();
		this.alternatemobilenumber = user.getAlternateMobileNumber();
		mapPermanentAddress(user);
		mapCorrespondenceAddress(user);
	}

	private void mapCorrespondenceAddress(User user) {
		if (user.getCorrespondenceAddress() != null) {
			this.correspondenceAddress = user.getCorrespondenceAddress().getAddress();
			this.correspondenceCity = user.getCorrespondenceAddress().getCity();
			this.correspondencePinCode = user.getCorrespondenceAddress().getPinCode();
		}
	}

	private void mapPermanentAddress(User user) {
		if (user.getPermanentAddress() != null) {
			this.permanentAddress = user.getPermanentAddress().getAddress();
			this.permanentCity = user.getPermanentAddress().getCity();
			this.permanentPinCode = user.getPermanentAddress().getPinCode();
		}
	}

	private Set<RoleRequest> convertDomainRolesToContract(Set<Role> roleEntities) {
		if (roleEntities == null)
			return new HashSet<>();
		return roleEntities.stream().map(RoleRequest::new).collect(Collectors.toSet());
	}

}
