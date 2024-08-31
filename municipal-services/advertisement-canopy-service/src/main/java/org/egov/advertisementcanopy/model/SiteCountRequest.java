package org.egov.advertisementcanopy.model;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SiteCountRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	private String tenantId;

}
