package org.egov.vendor.web.models.vendorcontract.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.Role;
<<<<<<< HEAD
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
=======
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
>>>>>>> master-LTS
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
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("uuid")
    private String uuid;

    @Size(max=64)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("userName")
    private String userName;

    @Size(max=64)
<<<<<<< HEAD
    @JsonProperty("password")
    private String password;

=======
    @SafeHtml
    @JsonProperty("password")
    private String password;

    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("salutation")
    private String salutation;

    @NotNull
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @Size(max=100)
    @Pattern(regexp = "^[a-zA-Z0-9 \\-'`\\.]*$", message = "Invalid name. Only alphabets and special characters -, ',`, .")
    @JsonProperty("name")
    private String name;

    @NotNull
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("gender")
    private String gender;

    // @NotNull
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
   // @Pattern(regexp = "^[0-9]{10}$", message = "MobileNumber should be 10 digit number")
    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @Size(max=128)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("emailId")
    private String emailId;

    @Size(max=50)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("altContactNumber")
    private String altContactNumber;

    @Size(max=10)
<<<<<<< HEAD
    @JsonProperty("pan")
    private String pan;

=======
    @SafeHtml
    @JsonProperty("pan")
    private String pan;

    @SafeHtml
>>>>>>> master-LTS
    @Pattern(regexp = "^[0-9]{12}$", message = "AdharNumber should be 12 digit number")
    @JsonProperty("aadhaarNumber")
    private String aadhaarNumber;

    @Size(max=300)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("permanentAddress")
    private String permanentAddress;

    @Size(max=300)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("permanentCity")
    private String permanentCity;

    @Size(max=10)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("permanentPinCode")
    private String permanentPincode;

    @Size(max=300)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("correspondenceCity")
    private String correspondenceCity;

    @Size(max=10)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("correspondencePinCode")
    private String correspondencePincode;

    @Size(max=300)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("correspondenceAddress")
    private String correspondenceAddress;

    @JsonProperty("active")
    private Boolean active;

    
    @JsonProperty("dob")
    private Long dob;

    @JsonProperty("pwdExpiryDate")
    private Long pwdExpiryDate;

    @Size(max=16)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("locale")
    private String locale;

    @Size(max=50)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("type")
    private String type;

    @Size(max=36)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("signature")
    private String signature;

    @JsonProperty("accountLocked")
    private Boolean accountLocked;

    @JsonProperty("roles")
    @Valid
    private List<Role> roles;

<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @Size(max=100)
    @JsonProperty("fatherOrHusbandName")
    private String fatherOrHusbandName;

    @JsonProperty("relationship")
    private GuardianRelation relationship;


    public enum GuardianRelation {
        FATHER, MOTHER, HUSBAND, OTHER;
    }
    
    @Size(max=32)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("bloodGroup")
    private String bloodGroup;

    @Size(max=300)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("identificationMark")
    private String identificationMark;

    @Size(max=36)
    @JsonProperty("photo")
    private String photo;

    @Size(max=64)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("createdDate")
    private Long createdDate;

    @Size(max=64)
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;

    @JsonProperty("lastModifiedDate")
    private Long lastModifiedDate;

<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("otpReference")
    private String otpReference;

    @Size(max=256)
    @NonNull
<<<<<<< HEAD
=======
    @SafeHtml
>>>>>>> master-LTS
    @JsonProperty("tenantId")
    private String tenantId;
    
   	
}
