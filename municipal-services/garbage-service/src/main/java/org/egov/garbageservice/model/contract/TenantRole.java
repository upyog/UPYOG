package org.egov.garbageservice.model.contract;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Maps a tenant (ULB) to the list of roles a user holds in that jurisdiction.
 * Used when building or validating user authorization across multi-tenant garbage deployments.
 * Ignores unknown JSON fields for forward-compatible API responses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantRole {

	@NotNull
	@CustomSafeHtml
    private String tenantId ;
    
	@NotNull
	private List<Role> roles;

	public String getTenantId() {	
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
