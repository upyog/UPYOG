package org.egov.advertisementcanopy.model;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SiteCreationRequest {
	
	  @JsonProperty("RequestInfo")
	   private RequestInfo requestInfo = null;
	  @JsonProperty("siteCreationData")
	  private SiteCreationData creationData;
}
