package org.egov.tl.web.models;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeLicenseActionRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;
    
	private List<String> applicationNumbers;
	
	private String tenantId;
	private Boolean isHistoryCall = false;
}
