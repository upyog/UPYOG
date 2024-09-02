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
@NoArgsConstructor
public class SearchCriteriaGarbageAccountRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	private SearchCriteriaGarbageAccount searchCriteriaGarbageAccount;

}
