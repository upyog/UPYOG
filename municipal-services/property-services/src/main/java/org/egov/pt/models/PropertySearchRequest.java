package org.egov.pt.models;

import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertySearchRequest {
	
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;
    
	private List<String> applicationNumbers;
	
	private String businessService;

}
