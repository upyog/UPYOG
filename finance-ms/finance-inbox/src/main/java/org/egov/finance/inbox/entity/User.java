
package org.egov.finance.inbox.entity;

import java.io.Serializable;
import java.util.List;

import org.egov.finance.inbox.customannotation.SafeHtml;

//This class is serialized to Redis
public class User implements Serializable {
    private static final long serialVersionUID = -1053170163821651014L;
    private Long id;
    @SafeHtml
    private String uuid;
    @SafeHtml
    private String userName;
    @SafeHtml
    private String userType;
    @SafeHtml
    private String name;
    @SafeHtml
    private String mobileNumber;
    @SafeHtml
    private String emailId;
    @SafeHtml
    private String locale;
    @SafeHtml
    private String type;
    private List<Role> roles;
    private boolean active;
    private String tenantId;

    public User() {
    }

    public User(Long id, String uuid, String userName, String name, String mobileNumber, String emailId, String locale,
            String type, List<Role> roles, boolean active, String tenantId) {
        this.id = id;
        this.uuid = uuid;
        this.userName = userName;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.emailId = emailId;
        this.locale = locale;
        this.type = type;
        this.roles = roles;
        this.active = active;
        this.tenantId = tenantId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", uuid=" + uuid + ", userName=" + userName + ", name=" + name + ", mobileNumber="
                + mobileNumber + ", emailId=" + emailId + ", locale=" + locale + ", type=" + type + ", roles=" + roles
                + ", active=" + active + ", tenantId=" + tenantId + "]";
    }

}