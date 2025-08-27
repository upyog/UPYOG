package org.egov.user.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang3.time.DateUtils;
import org.egov.user.config.*;
import org.egov.user.domain.exception.InvalidUserCreateException;
import org.egov.user.domain.exception.InvalidUserUpdateException;
import org.egov.user.domain.model.enums.BloodGroup;
import org.egov.user.domain.model.enums.Gender;
import org.egov.user.domain.model.enums.GuardianRelation;
import org.egov.user.domain.model.enums.UserType;
import org.hibernate.validator.constraints.Email;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.springframework.util.ObjectUtils.isEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class User {

    private Long id;
    private String uuid;
    private String digilockerid;
    private String access_token;

    @Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
    @Size(max = 50)
    private String tenantId;
    private String username;
    private String title;
    private String password;
    private String salutation;

    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
    private String guardian;

    private GuardianRelation guardianRelation;

    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
    @Size(max = 50)
    private String name;
    private Gender gender;
    private String mobileNumber;

    @Email
    private String emailId;
    private String altContactNumber;
    private String pan;
    private String aadhaarNumber;
    private Address permanentAddress;
    private Address correspondenceAddress;
    private Set<Address> addresses;
    private Boolean active;
    private Set<Role> roles;
    private Date dob;
    private Date passwordExpiryDate;
    private String locale = "en_IN";
    private UserType type;
    private BloodGroup bloodGroup;
    private String identificationMark;
    private String signature;
    private String photo;
    private Boolean accountLocked;
    private Long accountLockedDate;
    private Date lastModifiedDate;
    private Date createdDate;
    private String otpReference;
    private Long createdBy;
    private Long lastModifiedBy;
    private Long loggedInUserId;
    private String loggedInUserUuid;
    private boolean otpValidationMandatory;
    private boolean mobileValidationMandatory = true;
    private String alternateMobileNumber;
    private boolean digilockerRegistration;

    public User addAddressItem(Address addressItem) {
        if (this.addresses == null) {
            this.addresses = new HashSet<>();
        }
        this.addresses.add(addressItem);
        return this;
    }

    public User addRolesItem(Role roleItem) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(roleItem);
        return this;
    }

    public void validateNewUser() {
        validateNewUser(true);
    }

    public void validateNewUser(boolean createUserValidateName) {
        if (isUsernameAbsent()
                || (createUserValidateName && isNameAbsent())
                || isMobileNumberAbsent()
                || isActiveIndicatorAbsent()
                || isTypeAbsent()
                || isPermanentAddressInvalid()
                || isCorrespondenceAddressInvalid()
                || isRolesAbsent()
                || isOtpReferenceAbsent()
                || isTenantIdAbsent()) {
            throw new InvalidUserCreateException(this);
        }
    }

    /**
     * Validates user for v2 API with addresses array structure
     */
    public void validateNewUserV2(boolean createUserValidateName) {
        if (isUsernameAbsent()
                || (createUserValidateName && isNameAbsent())
                || isMobileNumberAbsent()
                || isActiveIndicatorAbsent()
                || isTypeAbsent()
                || isAddressesInvalid()
                || isRolesAbsent()
                || isOtpReferenceAbsent()
                || isTenantIdAbsent()) {
            throw new InvalidUserCreateException(this);
        }
    }

    /**
     * Simple validation for v2 API that skips old address validation
     */
    public void validateNewUserV2Simple(boolean createUserValidateName) {
        if (isUsernameAbsent()
                || (createUserValidateName && isNameAbsent())
                || isMobileNumberAbsent()
                || isActiveIndicatorAbsent()
                || isTypeAbsent()
                || isRolesAbsent()
                || isOtpReferenceAbsent()
                || isTenantIdAbsent()) {
            throw new InvalidUserCreateException(this);
        }
    }

    public void validateUserModification() {
        if (isPermanentAddressInvalid()
                || isCorrespondenceAddressInvalid()
                || isTenantIdAbsent()
        ) {
            throw new InvalidUserUpdateException(this);
        }
    }

    @JsonIgnore
    public boolean isCorrespondenceAddressInvalid() {
        return correspondenceAddress != null && correspondenceAddress.isInvalid();
    }

    @JsonIgnore
    public boolean isPermanentAddressInvalid() {
        return permanentAddress != null && permanentAddress.isInvalid();
    }

    @JsonIgnore
    public boolean isAddressesInvalid() {
        if (addresses == null || addresses.isEmpty()) {
            return false; // Addresses are optional in v2
        }
        return addresses.stream().anyMatch(address -> address != null && address.isInvalid());
    }

    @JsonIgnore
    public boolean isOtpReferenceAbsent() {
        return otpValidationMandatory && isEmpty(otpReference);
    }

    @JsonIgnore
    public boolean isTypeAbsent() {
        return isEmpty(type);
    }

    @JsonIgnore
    public boolean isActiveIndicatorAbsent() {
        return isEmpty(active);
    }

    @JsonIgnore
    public boolean isMobileNumberAbsent() {
        return mobileValidationMandatory && isEmpty(mobileNumber);
    }

    @JsonIgnore
    public boolean isNameAbsent() {
        return isEmpty(name);
    }

    @JsonIgnore
    public boolean isUsernameAbsent() {
        return isEmpty(username);
    }

    @JsonIgnore
    public boolean isTenantIdAbsent() {
        return isEmpty(tenantId);
    }

    @JsonIgnore
    public boolean isPasswordAbsent() {
        return isEmpty(password);
    }

    @JsonIgnore
    public boolean isRolesAbsent() {
        return CollectionUtils.isEmpty(roles) || roles.stream().anyMatch(r -> isEmpty(r.getCode()));
    }

    @JsonIgnore
    public boolean isIdAbsent() {
        return id == null;
    }

    public void nullifySensitiveFields() {
        username = null;
        type = null;
        mobileNumber = null;
        password = null;
        passwordExpiryDate = null;
        roles = null;
        accountLocked = null;
        accountLockedDate = null;
    }

    @JsonIgnore
    public boolean isLoggedInUserDifferentFromUpdatedUser() {
        return !id.equals(loggedInUserId) || !uuid.equals(loggedInUserUuid);
    }

    public void setRoleToCitizen() {
        type = UserType.CITIZEN;
        roles = Collections.singleton(Role.getCitizenRole());
    }

    public void updatePassword(String newPassword) {
        password = newPassword;
    }

    @JsonIgnore
    public OtpValidationRequest getOtpValidationRequest() {
        return OtpValidationRequest.builder()
                .mobileNumber(mobileNumber)
                .tenantId(tenantId)
                .otpReference(otpReference)
                .build();
    }

    @JsonIgnore
    public List<Address> getPermanentAndCorrespondenceAddresses() {
        final ArrayList<Address> addresses = new ArrayList<>();
        if (correspondenceAddress != null && correspondenceAddress.isNotEmpty()) {
            addresses.add(correspondenceAddress);
        }
        if (permanentAddress != null && permanentAddress.isNotEmpty()) {
            addresses.add(permanentAddress);
        }
        return addresses;
    }

    public void setDefaultPasswordExpiry(int expiryInDays) {
        if (passwordExpiryDate == null) {
            passwordExpiryDate = DateUtils.addDays(new Date(), expiryInDays);
        }
    }

    public void setActive(boolean isActive) {
        active = isActive;
    }

    public User toUser(User u) {
   	 
        User user = new User();
 
        user.setId(u.id);
        user.setUuid(u.uuid);
        user.setTenantId(u.tenantId);
        user.setUsername(u.username);
        user.setSalutation(u.salutation);
        user.setName(u.name);
        user.setMobileNumber(u.mobileNumber);
        user.setEmailId(u.emailId);
        user.setAltContactNumber(u.altContactNumber);
        user.setPan(u.pan);
        user.setAadhaarNumber(u.aadhaarNumber);
 
        // Gender conversion
        if (u.gender != null) {
            try {
                user.setGender(u.gender);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid gender value: " + u.gender);
            }
        }
 
        // BloodGroup conversion
        if (u.bloodGroup != null) {
            try {
                user.setBloodGroup(u.bloodGroup);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid blood group value: " + u.bloodGroup);
            }
        }
 
        user.setLocale(u.locale);
        user.setType(u.type);
        user.setAccountLocked(u.accountLocked);
        user.setAccountLockedDate(u.accountLockedDate);
        user.setDob(u.dob);
        user.setPasswordExpiryDate(u.getPasswordExpiryDate());
        user.setSignature(u.signature);
        user.setPhoto(u.photo);
        user.setIdentificationMark(u.identificationMark);
        user.setCreatedBy(u.createdBy);
        user.setCreatedDate(u.createdDate);
        user.setLastModifiedBy(u.lastModifiedBy);
        user.setLastModifiedDate(u.lastModifiedDate);
//        user.setGuardian(u.fatherOrHusbandName);
//        user.setGuardianRelation(u.relationship);
        user.setActive(u.active);
        user.setAlternateMobileNumber(u.alternateMobileNumber);
 
        // Address mappings
//        if (u.permanentAddress != null || u.permanentCity != null || u.permanentPinCode != null) {
//            Address permanentAddress = new Address();
//            permanentAddress.setAddress(u.permanentAddress);
//            permanentAddress.setCity(u.permanentCity);
//            permanentAddress.setPinCode(u.permanentPinCode);
//            user.setPermanentAddress(permanentAddress);
//        }
 
//        if (u.correspondenceAddress != null || u.correspondenceCity != null || u.correspondencePinCode != null) {
//            Address correspondenceAddress = new Address();
//            correspondenceAddress.setAddress(u.correspondenceAddress);
//            correspondenceAddress.setCity(u.correspondenceCity);
//            correspondenceAddress.setPinCode(u.correspondencePinCode);
//            user.setCorrespondenceAddress(correspondenceAddress);
//        }
 
        user.setAddresses(u.addresses);
 
        // Roles conversion
        if (u.roles != null) {
            Set<Role> roleEntities = u.roles.stream()
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


