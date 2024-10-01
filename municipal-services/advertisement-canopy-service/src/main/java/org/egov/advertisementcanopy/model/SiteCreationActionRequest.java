package org.egov.advertisementcanopy.model;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteCreationActionRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo =null;
	
	@JsonProperty("siteID")
	private List<String> siteId;
	
	private String businessService;

}
