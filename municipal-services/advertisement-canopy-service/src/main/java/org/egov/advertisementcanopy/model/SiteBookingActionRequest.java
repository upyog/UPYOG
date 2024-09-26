package org.egov.advertisementcanopy.model;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SiteBookingActionRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;
    
	private List<String> applicationNumbers;
	
}
