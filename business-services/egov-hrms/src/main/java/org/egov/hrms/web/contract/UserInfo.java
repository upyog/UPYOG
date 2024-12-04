package org.egov.hrms.web.contract;

import java.util.List;

import org.egov.hrms.model.Role;

import lombok.Data;

@Data
public class UserInfo {
	private Long id;
    private String userName;
    private String name;
    private String gender;
    private String mobileNumber;
    private String permanentAddress;
    private String permanentCity;
    private String permanentPinCode;
    private String correspondenceAddress;
    private String correspondenceCity;
    private String correspondencePinCode;
    private Boolean active;
    private String type;
    private Boolean accountLocked;
    private Long accountLockedDate;
    private String fatherOrHusbandName;
    private String tenantId;
    private String uuid;
    private String createdDate;
    private String lastModifiedDate;
    private String dob;
    private String pwdExpiryDate;
    private List<Role> roles;
}
