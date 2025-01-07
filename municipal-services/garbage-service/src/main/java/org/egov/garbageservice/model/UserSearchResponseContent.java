package org.egov.garbageservice.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.garbageservice.enums.GuardianRelation;
import org.egov.garbageservice.enums.UserType;

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

    public UserSearchResponseContent(UserV2 userV2) {

        this.id = userV2.getId();
        this.userName = userV2.getUsername();
        this.salutation = userV2.getSalutation();
        this.name = userV2.getName();
        this.gender = userV2.getGender() != null ? userV2.getGender().toString() : null;
        this.mobileNumber = userV2.getMobileNumber();
        this.emailId = userV2.getEmailId();
        this.altContactNumber = userV2.getAltContactNumber();
        this.pan = userV2.getPan();
        this.aadhaarNumber = userV2.getAadhaarNumber();
        this.active = userV2.getActive();
        this.dob = userV2.getDob();
        this.pwdExpiryDate = userV2.getPasswordExpiryDate();
        this.locale = userV2.getLocale();
        this.type = userV2.getType();
        this.accountLocked = userV2.getAccountLocked();
        this.accountLockedDate = userV2.getAccountLockedDate();
        this.signature = userV2.getSignature();
        this.bloodGroup = userV2.getBloodGroup() != null ? userV2.getBloodGroup().getValue() : null;
        this.photo = userV2.getPhoto();
        this.identificationMark = userV2.getIdentificationMark();
        this.createdBy = userV2.getCreatedBy();
        this.createdDate = userV2.getCreatedDate();
        this.lastModifiedBy = userV2.getLastModifiedBy();
        this.lastModifiedDate = userV2.getLastModifiedDate();
        this.tenantId = userV2.getTenantId();
        this.roles = convertDomainRolesToContract(userV2.getRoles());
        this.fatherOrHusbandName = userV2.getGuardian();
        this.relationship = userV2.getGuardianRelation();
        this.uuid = userV2.getUuid();
        this.addresses = userV2.getAddresses();
        this.alternatemobilenumber=userV2.getAlternateMobileNumber();
        mapPermanentAddress(userV2);
        mapCorrespondenceAddress(userV2);
    }

    private void mapCorrespondenceAddress(UserV2 userV2) {
        if (userV2.getCorrespondenceAddress() != null) {
            this.correspondenceAddress = userV2.getCorrespondenceAddress().getAddress();
            this.correspondenceCity = userV2.getCorrespondenceAddress().getCity();
            this.correspondencePinCode = userV2.getCorrespondenceAddress().getPinCode();
        }
    }

    private void mapPermanentAddress(UserV2 userV2) {
        if (userV2.getPermanentAddress() != null) {
            this.permanentAddress = userV2.getPermanentAddress().getAddress();
            this.permanentCity = userV2.getPermanentAddress().getCity();
            this.permanentPinCode = userV2.getPermanentAddress().getPinCode();
        }
    }


    private Set<RoleRequest> convertDomainRolesToContract(Set<RoleV2> roleEntities) {
        if (roleEntities == null) return new HashSet<>();
        return roleEntities.stream().map(RoleRequest::new).collect(Collectors.toSet());
    }

}
