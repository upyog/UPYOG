package org.egov.garbageservice.model;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TotalCountRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	private String tenantId;

}
