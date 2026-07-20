package org.egov.garbageservice.model;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
/**
 * Request wrapper for searching local GarbageBill records via GarbageBillController.
 * Combines RequestInfo with GarbageBillSearchCriteria filters.
 */
@NoArgsConstructor
public class SearchGarbageBillRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	private GarbageBillSearchCriteria garbageBillSearchCriteria;

}
