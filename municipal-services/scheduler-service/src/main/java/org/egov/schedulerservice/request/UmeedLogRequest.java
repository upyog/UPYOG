package org.egov.schedulerservice.request;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UmeedLogRequest {
	
	private String date;

    private String serviceType;

    private JsonNode requestPayload;

    private JsonNode responsePayload;


}
