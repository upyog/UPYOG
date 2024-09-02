package org.egov.advertisementcanopy.model;

import org.egov.common.contract.response.ResponseInfo;

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
public class SiteCountResponse {
	
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseinfo;
	
	private SiteCountData siteCountData;
	

}
