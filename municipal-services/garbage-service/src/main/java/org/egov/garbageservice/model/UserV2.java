package org.egov.garbageservice.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.egov.garbageservice.enums.BloodGroup;
import org.egov.garbageservice.enums.Gender;
import org.egov.garbageservice.enums.GuardianRelation;
import org.egov.garbageservice.enums.UserType;
import org.egov.garbageservice.util.UserServiceConstants;
import org.egov.tracer.annotations.CustomSafeHtml;
import org.hibernate.validator.constraints.Email;

import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
/**
 * User profile model aligned with eGov user service v2 schema for create/update/search.
 * Includes demographics, addresses, roles, and audit fields used when linking garbage accounts to users.
 */
@Builder(toBuilder = true)
public class UserV2 {

    private Long id;
    @CustomSafeHtml
    private String uuid;

    @Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
    @Size(max = 50)
    @CustomSafeHtml
    private String tenantId;
    @CustomSafeHtml
    private String username;
    @CustomSafeHtml
    private String title;
    @CustomSafeHtml
    private String password;
    @CustomSafeHtml
    private String salutation;

    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
    @CustomSafeHtml
    private String guardian;

    private GuardianRelation guardianRelation;

    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
    @Size(max = 50)
    @CustomSafeHtml
    private String name;
    private Gender gender;
    @CustomSafeHtml
    private String mobileNumber;

    @Email
    @CustomSafeHtml
    private String emailId;
    @CustomSafeHtml
    private String altContactNumber;
    @CustomSafeHtml
    private String pan;
    @CustomSafeHtml
    private String aadhaarNumber;
    private Address permanentAddress;
    private Address correspondenceAddress;
    private Set<Address> addresses;
    private Boolean active;
    private Set<RoleV2> roles;
    private Date dob;
    private Date passwordExpiryDate;
    @CustomSafeHtml
    private String locale = "en_IN";
    private UserType type;
    private BloodGroup bloodGroup;
    @CustomSafeHtml
    private String identificationMark;
    @CustomSafeHtml
    private String signature;
    @CustomSafeHtml
    private String photo;
    private Boolean accountLocked;
    private Long accountLockedDate;
    private Date lastModifiedDate;
    private Date createdDate;
    @CustomSafeHtml
    private String otpReference;
    private Long createdBy;
    private Long lastModifiedBy;
    private Long loggedInUserId;
    @CustomSafeHtml
    private String loggedInUserUuid;
    private boolean otpValidationMandatory;
    private boolean mobileValidationMandatory = true;
    @CustomSafeHtml
    private String alternateMobileNumber;

    /**
     * Sets the active.
     *
     * @param active the active to set
     */

    public void setActive(boolean isActive) {
        active = isActive;
    }
}
