package org.upyog.tp.web.models.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.egov.common.contract.request.Role;
import org.upyog.tp.validation.*;
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

public class User   {

        @JsonProperty("id")
        private Long id;

        @Size(max=64)
        @SanitizeHtml
        @JsonProperty("uuid")
        private String uuid;

        @Size(max=64)
        @SanitizeHtml
        @JsonProperty("userName")
        private String userName;

        @Size(max=64)
        @SanitizeHtml
        @JsonProperty("password")
        private String password;

        @JsonProperty("salutation")
        @SanitizeHtml
        private String salutation;

        @NotNull
        @SanitizeHtml
        @Size(max=100)
       // @Pattern(regexp = "^[^\\$\"'<>?\\\\~`!@#$%^()+={}\\[\\]*,:;“”‘’]*$", message = "Invalid name. Only alphabets and special characters -, ',`, .")
        @JsonProperty("name")
        private String name;

        @SanitizeHtml
        @JsonProperty("gender")
        private String gender;

       // @Pattern(regexp = "(^[6-9][0-9]{9}$)", message = "Inavlid mobile number, should start with 6-9 and contain ten digits of 0-9")
        @NotNull
        @SanitizeHtml
        @JsonProperty("mobileNumber")
        private String mobileNumber;

        @Size(max=128)
        @SanitizeHtml
        @JsonProperty("emailId")
        private String emailId;

        @Size(max=50)
        @SanitizeHtml
        @JsonProperty("altContactNumber")
        private String altContactNumber;

        @Size(max=10)
        @SanitizeHtml
        @JsonProperty("pan")
        private String pan;

        @SanitizeHtml
        @Pattern(regexp = "^[0-9]{12}$", message = "AdharNumber should be 12 digit number")
        @JsonProperty("aadhaarNumber")
        private String aadhaarNumber;

        @Size(max=300)
        @SanitizeHtml
        @JsonProperty("permanentAddress")
        private String permanentAddress;

        @Size(max=300)
        @SanitizeHtml
        @JsonProperty("permanentCity")
        private String permanentCity;

        @Size(max=10)
        @SanitizeHtml
        @JsonProperty("permanentPinCode")
        private String permanentPincode;

        @Size(max=300)
        @SanitizeHtml
        @JsonProperty("correspondenceCity")
        private String correspondenceCity;

        @Size(max=10)
        @SanitizeHtml
        @JsonProperty("correspondencePinCode")
        private String correspondencePincode;

        @Size(max=300)
        @SanitizeHtml
        @JsonProperty("correspondenceAddress")
        private String correspondenceAddress;

        @JsonProperty("active")
        private Boolean active;

        @JsonProperty("dob")
        private Long dob;

        @JsonProperty("pwdExpiryDate")
        private Long pwdExpiryDate;

        @Size(max=16)
        @SanitizeHtml
        @JsonProperty("locale")
        private String locale;

        @Size(max=50)
        @SanitizeHtml
        @JsonProperty("type")
        private String type;

        @Size(max=36)
        @SanitizeHtml
        @JsonProperty("signature")
        private String signature;

        @JsonProperty("accountLocked")
        private Boolean accountLocked;

        @JsonProperty("roles")
        @Valid
        private List<Role> roles;

        @Size(max=100)
        @SanitizeHtml
        @JsonProperty("fatherOrHusbandName")
        private String fatherOrHusbandName;

        @Size(max=32)
        @SanitizeHtml
        @JsonProperty("bloodGroup")
        private String bloodGroup;

        @Size(max=300)
        @SanitizeHtml
        @JsonProperty("identificationMark")
        private String identificationMark;

        @Size(max=36)
        @SanitizeHtml
        @JsonProperty("photo")
        private String photo;

        @Size(max=64)
        @SanitizeHtml
        @JsonProperty("createdBy")
        private String createdBy;

        @JsonProperty("createdDate")
        private Long createdDate;
        @Size(max=64)

        @SanitizeHtml
        @JsonProperty("lastModifiedBy")
        private String lastModifiedBy;

        @JsonProperty("lastModifiedDate")
        private Long lastModifiedDate;

        @Size(max=256)
        @SanitizeHtml
        @JsonProperty("tenantId")
        private String tenantId;
        
        @Size(max=50)
        @SanitizeHtml
        @JsonProperty("alternatemobilenumber")
        private String alternatemobilenumber;

        @JsonProperty("addresses") // Make sure this is present
        private List<AddressV2> addresses;

        public User addAddressItem(AddressV2 addressItem) {
            if (this.addresses == null) {
                this.addresses = new ArrayList<>();
            }
            this.addresses.add(addressItem);
            return this;
        }

        public User addRolesItem(Role rolesItem) {
            if (this.roles == null) {
            this.roles = new ArrayList<>();
            }
        this.roles.add(rolesItem);
        return this;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                User user = (User) o;
                return Objects.equals(uuid, user.uuid) &&
                        Objects.equals(name, user.name) &&
                        Objects.equals(mobileNumber, user.mobileNumber);
        }

        @Override
        public int hashCode() {

                return Objects.hash(uuid, name, mobileNumber);
        }
}
 
