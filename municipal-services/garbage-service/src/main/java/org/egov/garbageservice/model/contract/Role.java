package org.egov.garbageservice.model.contract;

import jakarta.validation.constraints.NotNull;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


/**
 * Role definition assigned to users for access control in garbage-service integrations.
 * Holds human-readable name, system code, and description; attached to User and TenantRole lists.
 * Unknown JSON properties are ignored to stay compatible with user-service API changes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
	
	@NotNull
	@CustomSafeHtml
	private String name ;
	
	@CustomSafeHtml
	private String code ;

	@CustomSafeHtml
	private String description ;
		
	public Role(final String name) {
	    this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
