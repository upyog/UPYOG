package org.upyog.tp.web.models.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

<<<<<<< HEAD
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.egov.common.contract.request.Role;
import org.upyog.tp.validation.*;
=======
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.egov.common.contract.request.Role;
import org.hibernate.validator.constraints.SafeHtml;
>>>>>>> master-LTS
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
<<<<<<< HEAD

=======
	
>>>>>>> master-LTS
        @JsonProperty("id")
        private Long id;

        @Size(max=64)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("uuid")
        private String uuid;

        @Size(max=64)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("userName")
        private String userName;

        @Size(max=64)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("password")
        private String password;

        @JsonProperty("salutation")
<<<<<<< HEAD
        @SanitizeHtml
        private String salutation;

        @NotNull
        @SanitizeHtml
=======
        @SafeHtml
        private String salutation;

        @NotNull
        @SafeHtml
>>>>>>> master-LTS
        @Size(max=100)
       // @Pattern(regexp = "^[^\\$\"'<>?\\\\~`!@#$%^()+={}\\[\\]*,:;“”‘’]*$", message = "Invalid name. Only alphabets and special characters -, ',`, .")
        @JsonProperty("name")
        private String name;

<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("gender")
        private String gender;

       // @Pattern(regexp = "(^[6-9][0-9]{9}$)", message = "Inavlid mobile number, should start with 6-9 and contain ten digits of 0-9")
        @NotNull
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("mobileNumber")
        private String mobileNumber;

        @Size(max=128)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("emailId")
        private String emailId;

        @Size(max=50)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("altContactNumber")
        private String altContactNumber;

        @Size(max=10)
<<<<<<< HEAD
        @SanitizeHtml
        @JsonProperty("pan")
        private String pan;

        @SanitizeHtml
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
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("permanentAddress")
        private String permanentAddress;

        @Size(max=300)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("permanentCity")
        private String permanentCity;

        @Size(max=10)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("permanentPinCode")
        private String permanentPincode;

        @Size(max=300)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("correspondenceCity")
        private String correspondenceCity;

        @Size(max=10)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("correspondencePinCode")
        private String correspondencePincode;

        @Size(max=300)
<<<<<<< HEAD
        @SanitizeHtml
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
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("locale")
        private String locale;

        @Size(max=50)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("type")
        private String type;

        @Size(max=36)
<<<<<<< HEAD
        @SanitizeHtml
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

        @Size(max=100)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("fatherOrHusbandName")
        private String fatherOrHusbandName;

        @Size(max=32)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("bloodGroup")
        private String bloodGroup;

        @Size(max=300)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("identificationMark")
        private String identificationMark;

        @Size(max=36)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("photo")
        private String photo;

        @Size(max=64)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("createdBy")
        private String createdBy;

        @JsonProperty("createdDate")
        private Long createdDate;

        @Size(max=64)
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("lastModifiedBy")
        private String lastModifiedBy;

        @JsonProperty("lastModifiedDate")
        private Long lastModifiedDate;

        @Size(max=256)
<<<<<<< HEAD
        @SanitizeHtml
        @JsonProperty("tenantId")
        private String tenantId;

        @Size(max=50)
        @SanitizeHtml
=======
        @SafeHtml
        @JsonProperty("tenantId")
        private String tenantId;
        
        @Size(max=50)
        @SafeHtml
>>>>>>> master-LTS
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
<<<<<<< HEAD
=======

>>>>>>> master-LTS
