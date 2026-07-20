package org.egov.garbageservice.model;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for calculate-fee and workflow-actions API calls on garbage accounts.
 * Returns application details, payable amounts, available actions, and related bill summaries.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GarbageAccountActionResponse {
	
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	private List<GarbageAccountDetail> applicationDetails;
}
