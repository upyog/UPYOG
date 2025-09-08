package org.hpud.model;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UmeedLogRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	private String date;

    private String serviceType;

    private JsonNode requestPayload;

    private JsonNode responsePayload;

}
