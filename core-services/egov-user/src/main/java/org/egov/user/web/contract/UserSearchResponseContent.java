package org.egov.user.web.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.user.domain.model.Address;
import org.egov.user.domain.model.Role;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.enums.BloodGroup;
import org.egov.user.domain.model.enums.Gender;
import org.egov.user.domain.model.enums.GuardianRelation;
import org.egov.user.domain.model.enums.UserType;

import java.util.*;
import java.util.stream.Collectors;

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
    private String digilockerid;

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
        this.digilockerid = user.getDigilockerid();
        this.addresses = user.getAddresses();
        this.alternatemobilenumber=user.getAlternateMobileNumber();
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
        if (roleEntities == null) return new HashSet<>();
        return roleEntities.stream().map(RoleRequest::new).collect(Collectors.toSet());
    }

    
    public User toUser() {
    	 
        User user = new User();
 
        user.setId(this.id);
        user.setUuid(this.uuid);
        user.setDigilockerid(this.digilockerid);
        user.setTenantId(this.tenantId);
        user.setUsername(this.userName);
        user.setSalutation(this.salutation);
        user.setName(this.name);
        user.setMobileNumber(this.mobileNumber);
        user.setEmailId(this.emailId);
        user.setAltContactNumber(this.altContactNumber);
        user.setPan(this.pan);
        user.setAadhaarNumber(this.aadhaarNumber);
 
        // Gender conversion
        if (this.gender != null) {
            try {
                user.setGender(Gender.valueOf(this.gender.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid gender value: " + this.gender);
            }
        }
 
        // BloodGroup conversion
        if (this.bloodGroup != null) {
            try {
                user.setBloodGroup(BloodGroup.valueOf(this.bloodGroup.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid blood group value: " + this.bloodGroup);
            }
        }
 
        user.setLocale(this.locale);
        user.setType(this.type);
        user.setAccountLocked(this.accountLocked);
        user.setAccountLockedDate(this.accountLockedDate);
        user.setDob(this.dob);
        user.setPasswordExpiryDate(this.pwdExpiryDate);
        user.setSignature(this.signature);
        user.setPhoto(this.photo);
        user.setIdentificationMark(this.identificationMark);
        user.setCreatedBy(this.createdBy);
        user.setCreatedDate(this.createdDate);
        user.setLastModifiedBy(this.lastModifiedBy);
        user.setLastModifiedDate(this.lastModifiedDate);
        user.setGuardian(this.fatherOrHusbandName);
        user.setGuardianRelation(this.relationship);
        user.setActive(this.active);
        user.setAlternateMobileNumber(this.alternatemobilenumber);
 
        // Address mappings
        if (this.permanentAddress != null || this.permanentCity != null || this.permanentPinCode != null) {
            Address permanentAddress = new Address();
            permanentAddress.setAddress(this.permanentAddress);
            permanentAddress.setCity(this.permanentCity);
            permanentAddress.setPinCode(this.permanentPinCode);
            user.setPermanentAddress(permanentAddress);
        }
 
        if (this.correspondenceAddress != null || this.correspondenceCity != null || this.correspondencePinCode != null) {
            Address correspondenceAddress = new Address();
            correspondenceAddress.setAddress(this.correspondenceAddress);
            correspondenceAddress.setCity(this.correspondenceCity);
            correspondenceAddress.setPinCode(this.correspondencePinCode);
            user.setCorrespondenceAddress(correspondenceAddress);
        }
 
        user.setAddresses(this.addresses);
 
        // Roles conversion
        if (this.roles != null) {
            Set<Role> roleEntities = this.roles.stream()
                    .map(roleRequest -> {
                        Role role = new Role();
                        role.setCode(roleRequest.getCode());
                        role.setName(roleRequest.getName());
                        return role;
                    })
                    .collect(Collectors.toSet());
            user.setRoles(roleEntities);
        }
 
        return user;
    }
}
