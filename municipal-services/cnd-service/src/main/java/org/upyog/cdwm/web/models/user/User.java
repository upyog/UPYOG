package org.upyog.cdwm.web.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.springframework.util.CollectionUtils;
import org.upyog.cdwm.web.models.user.enums.UserType;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

import static org.springframework.util.ObjectUtils.isEmpty;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
public class User {

    private Long id;
    private String uuid;

    @Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
    @Size(max = 50)
    private String tenantId;
    private String userName;
    private String title;
    private String salutation;

    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
    private String guardian;

    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
    @Size(max = 50)
    private String name;
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
    private List<Role> roles;
    private Date dob;
    private String locale = "en_IN";
    private UserType type;
    private String identificationMark;
    private String signature;
    private String photo;
    private Boolean accountLocked;
    private Long accountLockedDate;
    private Date lastModifiedDate;
    private Date createdDate;
    private Long createdBy;
    private Long lastModifiedBy;
    private String alternateMobileNumber;

    public User addAddressItem(Address addressItem) {
        if (this.addresses == null) {
            this.addresses = new HashSet<>();
        }
        this.addresses.add(addressItem);
        return this;
    }

    public User addRolesItem(Role roleItem) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(roleItem);
        return this;
    }

    // TODO: might be used in future
    // below methods are used to validate the user object before saving it to the database
    public void validateNewUser() {
        validateNewUser(true);
    }

    public void validateNewUser(boolean createUserValidateName) {
        if (isUsernameAbsent()
                || (createUserValidateName && isNameAbsent())

                || isActiveIndicatorAbsent()
                || isTypeAbsent()
                || isPermanentAddressInvalid()
                || isCorrespondenceAddressInvalid()
                || isRolesAbsent()

                || isTenantIdAbsent()) {
            throw new IllegalArgumentException("Invalid user creation request: missing required fields.");
        }
    }

    public void validateUserModification() {
        if (isPermanentAddressInvalid()
                || isCorrespondenceAddressInvalid()
                || isTenantIdAbsent()
        ) {
            throw new IllegalArgumentException("Invalid user update request: missing required fields.");
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
    public boolean isTypeAbsent() {
        return isEmpty(type);
    }

    @JsonIgnore
    public boolean isActiveIndicatorAbsent() {
        return isEmpty(active);
    }

    @JsonIgnore
    public boolean isNameAbsent() {
        return isEmpty(name);
    }

    @JsonIgnore
    public boolean isUsernameAbsent() {
        return isEmpty(userName);
    }

    @JsonIgnore
    public boolean isTenantIdAbsent() {
        return isEmpty(tenantId);
    }

    @JsonIgnore
    public boolean isRolesAbsent() {
        return CollectionUtils.isEmpty(roles) || roles.stream().anyMatch(r -> isEmpty(r.getCode()));
    }

    public void setActive(boolean isActive) {
        active = isActive;
    }
}


