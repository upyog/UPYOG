package org.egov.vendor.web.models.vendorcontract.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.Role;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@JsonProperty("id")
    private Long id;

    @Size(max=64)
    @JsonProperty("uuid")
    private String uuid;

    @Size(max=64)
    @JsonProperty("userName")
    private String userName;

    @Size(max=64)
    @JsonProperty("password")
    private String password;

    @JsonProperty("salutation")
    private String salutation;

    @NotNull
    @Size(max=100)
    @Pattern(regexp = "^[a-zA-Z0-9 \\-'`\\.]*$", message = "Invalid name. Only alphabets and special characters -, ',`, .")
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("gender")
    private String gender;

    // @NotNull
   // @Pattern(regexp = "^[0-9]{10}$", message = "MobileNumber should be 10 digit number")
    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @Size(max=128)
    @JsonProperty("emailId")
    private String emailId;

    @Size(max=50)
    @JsonProperty("altContactNumber")
    private String altContactNumber;

    @Size(max=10)
    @JsonProperty("pan")
    private String pan;

    @Pattern(regexp = "^[0-9]{12}$", message = "AdharNumber should be 12 digit number")
    @JsonProperty("aadhaarNumber")
    private String aadhaarNumber;

    @Size(max=300)
    @JsonProperty("permanentAddress")
    private String permanentAddress;

    @Size(max=300)
    @JsonProperty("permanentCity")
    private String permanentCity;

    @Size(max=10)
    @JsonProperty("permanentPinCode")
    private String permanentPincode;

    @Size(max=300)
    @JsonProperty("correspondenceCity")
    private String correspondenceCity;

    @Size(max=10)
    @JsonProperty("correspondencePinCode")
    private String correspondencePincode;

    @Size(max=300)
    @JsonProperty("correspondenceAddress")
    private String correspondenceAddress;

    @JsonProperty("active")
    private Boolean active;

    
    @JsonProperty("dob")
    private Long dob;

    @JsonProperty("pwdExpiryDate")
    private Long pwdExpiryDate;

    @Size(max=16)
    @JsonProperty("locale")
    private String locale;

    @Size(max=50)
    @JsonProperty("type")
    private String type;

    @Size(max=36)
    @JsonProperty("signature")
    private String signature;

    @JsonProperty("accountLocked")
    private Boolean accountLocked;

    @JsonProperty("roles")
    @Valid
    private List<Role> roles;

    @Size(max=100)
    @JsonProperty("fatherOrHusbandName")
    private String fatherOrHusbandName;

    @JsonProperty("relationship")
    private GuardianRelation relationship;


    public enum GuardianRelation {
        FATHER, MOTHER, HUSBAND, OTHER;
    }
    
    @Size(max=32)
    @JsonProperty("bloodGroup")
    private String bloodGroup;

    @Size(max=300)
    @JsonProperty("identificationMark")
    private String identificationMark;

    @Size(max=36)
    @JsonProperty("photo")
    private String photo;

    @Size(max=64)
    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("createdDate")
    private Long createdDate;

    @Size(max=64)
    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;

    @JsonProperty("lastModifiedDate")
    private Long lastModifiedDate;

    @JsonProperty("otpReference")
    private String otpReference;

    @Size(max=256)
    @NonNull
    @JsonProperty("tenantId")
    private String tenantId;
    
   	
}
