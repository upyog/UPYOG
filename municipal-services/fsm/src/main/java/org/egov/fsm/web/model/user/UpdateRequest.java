package org.egov.fsm.web.model.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.Role;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UpdateRequest object that matches the structure returned by egov-user service
 * from /users/_updatenovalidate endpoint
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("salutation")
    private String salutation;

    @JsonProperty("name")
    private String name;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @JsonProperty("emailId")
    private String emailId;

    @JsonProperty("altContactNumber")
    private String altContactNumber;

    @JsonProperty("pan")
    private String pan;

    @JsonProperty("aadhaarNumber")
    private String aadhaarNumber;

    @JsonProperty("permanentAddress")
    private String permanentAddress;

    @JsonProperty("permanentCity")
    private String permanentCity;

    @JsonProperty("permanentPinCode")
    private String permanentPinCode;

    @JsonProperty("correspondenceAddress")
    private String correspondenceAddress;

    @JsonProperty("correspondenceCity")
    private String correspondenceCity;

    @JsonProperty("correspondencePinCode")
    private String correspondencePinCode;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("type")
    private String type;

    @JsonProperty("accountLocked")
    private Boolean accountLocked;

    @JsonProperty("accountLockedDate")
    private Long accountLockedDate;

    @JsonProperty("fatherOrHusbandName")
    private String fatherOrHusbandName;

    @JsonProperty("signature")
    private String signature;

    @JsonProperty("bloodGroup")
    private String bloodGroup;

    @JsonProperty("photo")
    private String photo;

    @JsonProperty("identificationMark")
    private String identificationMark;

    @JsonProperty("createdBy")
    private Long createdBy;

    @JsonProperty("password")
    private String password;

    @JsonProperty("otpReference")
    private String otpReference;

    @JsonProperty("lastModifiedBy")
    private Long lastModifiedBy;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("alternatemobilenumber")
    private String alternatemobilenumber;

    @JsonProperty("roles")
    private Set<Role> roles;

    @JsonProperty("uuid")
    private String uuid;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("createdDate")
    private Long createdDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("lastModifiedDate")
    private Long lastModifiedDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("dob")
    private Long dob;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonProperty("pwdExpiryDate")
    private Long pwdExpiryDate;

    /**
     * Converts UpdateRequest to User object
     * @return User object
     */
    public User toUser() {
        User user = User.builder()
                .id(this.id)
                .userName(this.userName)
                .salutation(this.salutation)
                .name(this.name)
                .gender(this.gender)
                .mobileNumber(this.mobileNumber)
                .emailId(this.emailId)
                .altContactNumber(this.altContactNumber)
                .pan(this.pan)
                .aadhaarNumber(this.aadhaarNumber)
                .active(this.active)
                .locale(this.locale)
                .type(this.type)
                .accountLocked(this.accountLocked)
                .bloodGroup(this.bloodGroup)
                .identificationMark(this.identificationMark)
                .photo(this.photo)
                .createdBy(this.createdBy != null ? String.valueOf(this.createdBy) : null)
                .lastModifiedBy(this.lastModifiedBy != null ? String.valueOf(this.lastModifiedBy) : null)
                .tenantId(this.tenantId)
                .uuid(this.uuid)
                .createdDate(this.createdDate)
                .lastModifiedDate(this.lastModifiedDate)
                .dob(this.dob)
                .pwdExpiryDate(this.pwdExpiryDate)
                .build();

        // Convert roles from Set to List
        if (this.roles != null) {
            List<org.egov.common.contract.request.Role> roleList = new ArrayList<>();
            for (org.egov.common.contract.request.Role role : this.roles) {
                roleList.add(role);
            }
            user.setRoles(roleList);
        }

        return user;
    }
}
