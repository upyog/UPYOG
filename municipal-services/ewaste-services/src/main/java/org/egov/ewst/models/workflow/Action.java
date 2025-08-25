package org.egov.ewst.models.workflow;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.egov.ewst.models.AuditDetails;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an action in the workflow of the Ewaste application.
 * This class contains details about the action such as tenant ID, current state, next state, roles, etc.
 */
@SuppressWarnings("deprecation")
@ApiModel(description = "A Object holds the basic data for a Action in workflow")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = { "tenantId", "currentState", "action" })
public class Action {

	@Size(max = 256)
	@SafeHtml
	@JsonProperty("uuid")
	private String uuid;

	@Size(max = 256)
	@SafeHtml
	@JsonProperty("tenantId")
	private String tenantId;

	@Size(max = 256)
	@SafeHtml
	@JsonProperty("currentState")
	private String currentState;

	@Size(max = 256)
	@SafeHtml
	@JsonProperty("action")
	private String action;

	@Size(max = 256)
	@SafeHtml
	@JsonProperty("nextState")
	private String nextState;

	@Size(max = 1024)
	@JsonProperty("roles")
	@Valid
	private List<String> roles;

	private AuditDetails auditDetails;

	/**
	 * Adds a role to the list of roles associated with the action.
	 *
	 * @param rolesItem the role to add
	 * @return the updated Action object
	 */
	public Action addRolesItem(String rolesItem) {
		if (this.roles == null) {
			this.roles = new ArrayList<>();
		}
		this.roles.add(rolesItem);
		return this;
	}

}
