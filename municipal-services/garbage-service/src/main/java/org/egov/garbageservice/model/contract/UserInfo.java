package org.egov.garbageservice.model.contract;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Lightweight logged-in user snapshot embedded in RequestInfo across eGov APIs.
 * Serializable profile with id, userName, name, type, tenant roles, and token details.
 * Used when garbage-service forwards authenticated context to downstream services.
 */
@Setter
@Getter
@AllArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -6099520777478122089L;

    @CustomSafeHtml
    private String id;

    @NotNull
    @CustomSafeHtml
    private String userName;

    @CustomSafeHtml
    private String name;

    @CustomSafeHtml
    private String type;

    @CustomSafeHtml
    private String mobile;

    @CustomSafeHtml
    private String email;
    
    @CustomSafeHtml
    private String uuid;
	
    @CustomSafeHtml
    private String password;

    @CustomSafeHtml
    private String idToken;
    
    @NotNull
    private List<Role> primaryrole;

    private List<TenantRole> additionalroles;	
    
    @NotNull
    @CustomSafeHtml
    private String tenantId;
//
//    public UserInfo(final List<Role> primaryrole, final String id, final String userName, final String name, final String email,
//            final String mobile, final String type, final String tenantId) {
//        super();
//        this.primaryrole = primaryrole;
//        this.id = id;
//        this.userName = userName;
//        this.name = name;
//        this.email = email;
//        this.mobile = mobile;
//        this.type = type;
//        this.tenantId = tenantId;
//    }
//
//    public UserInfo() {
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public String getTenantId() {
//        return tenantId;
//    }
//
//	public String getMobile() {
//		return mobile;
//	}
//
//	public void setMobile(String mobile) {
//		this.mobile = mobile;
//	}
//
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	public String getUuid() {
//		return uuid;
//	}
//
//	public void setUuid(String uuid) {
//		this.uuid = uuid;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public String getIdToken() {
//		return idToken;
//	}
//
//	public void setIdToken(String idToken) {
//		this.idToken = idToken;
//	}
//
//	public List<Role> getPrimaryrole() {
//		return primaryrole;
//	}
//
//	public void setPrimaryrole(List<Role> primaryrole) {
//		this.primaryrole = primaryrole;
//	}
//
//	public List<TenantRole> getAdditionalroles() {
//		return additionalroles;
//	}
//
//	public void setAdditionalroles(List<TenantRole> additionalroles) {
//		this.additionalroles = additionalroles;
//	}
//
//	public void setUserName(String userName) {
//		this.userName = userName;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}
//
//	public void setTenantId(String tenantId) {
//		this.tenantId = tenantId;
//	}
//
//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
//
//    @Override
//    public String toString() {
//        return "UserInfo [id=" + id + ", userName=" + userName + ", name=" + name + ", type=" + type + ", mobile=" + mobile
//                + ", email=" + email + ", uuid=" + uuid + ", password=" + password + ", idToken=" + idToken + ", primaryrole="
//                + primaryrole + ", additionalroles=" + additionalroles + ", tenantId=" + tenantId + "]";
//    }

}