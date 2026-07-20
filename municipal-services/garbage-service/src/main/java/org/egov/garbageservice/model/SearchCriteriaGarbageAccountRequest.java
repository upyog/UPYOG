package org.egov.garbageservice.model;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body combining RequestInfo with SearchCriteriaGarbageAccount for account search APIs.
 * Also used when creating users for garbage accounts from search context.
 */
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class SearchCriteriaGarbageAccountRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	private SearchCriteriaGarbageAccount searchCriteriaGarbageAccount;

	@Builder.Default
	private Boolean isSchedulerCall = false;
	
	private Boolean isUserUuidNull;

}
