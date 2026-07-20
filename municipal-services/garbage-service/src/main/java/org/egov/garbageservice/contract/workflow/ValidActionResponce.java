package org.egov.garbageservice.contract.workflow;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.tracer.annotations.CustomSafeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Response model for workflow “valid actions” lookup for a business application.
 *
 * Behavior:
 * - Returns actions the current user may perform (action strings and {@link Action} nextValidAction list).
 * - Indicates whether the application record is editable in the current state via isUpdatable.
 * - Includes businessService and moduleName context for the garbage workflow module.
 * - Deserialized from POST response in {@link WorkflowService#getValidAction}.
 *
 * Notes:
 * - Class name uses legacy spelling {@code Responce} (Response); kept for API compatibility.
 * - On failure, WorkflowService may return an empty builder instance rather than throwing.
 * - Used to drive UI/action buttons before submitting a {@link ProcessInstanceRequest}.
 */
@Data
public class ValidActionResponce {
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("nextValidAction")
	@Builder.Default
	private List<Action> nextValidAction = new ArrayList<>();

	@JsonProperty("action")
	@Builder.Default
	private List<String> action = new ArrayList<>();

	@JsonProperty("isUpdatable")
	@Builder.Default
	private Boolean isUpdatable = false;

	@JsonProperty("businessService")
	@CustomSafeHtml
	private String businessService;

	@JsonProperty("moduleName")
	@CustomSafeHtml
	private String moduleName;
}