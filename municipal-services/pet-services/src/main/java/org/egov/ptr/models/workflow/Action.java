package org.egov.ptr.models.workflow;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import org.egov.ptr.models.AuditDetails;
import org.egov.ptr.validator.SanitizeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "A Object holds the basic data for a Action in workflow")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = { "tenantId", "currentState", "action" })
public class Action {

	@Size(max = 256)
	@SanitizeHtml
	@JsonProperty("uuid")
	private String uuid;

	@Size(max = 256)
	@SanitizeHtml
	@JsonProperty("tenantId")
	private String tenantId;

	@Size(max = 256)
	@SanitizeHtml
	@JsonProperty("currentState")
	private String currentState;

	@Size(max = 256)
	@SanitizeHtml
	@JsonProperty("action")
	private String action;

	@Size(max = 256)
	@SanitizeHtml
	@JsonProperty("nextState")
	private String nextState;

	@Size(max = 1024)
	@JsonProperty("roles")
	@Valid
	private List<String> roles;

	private AuditDetails auditDetails;

	public Action addRolesItem(String rolesItem) {
		if (this.roles == null) {
			this.roles = new ArrayList<>();
		}
		this.roles.add(rolesItem);
		return this;
	}

}
