package org.egov.schedulerservice.request;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UmeedDashboardRequest {

	private RequestInfo requestInfo;
	@JsonProperty("Data")
	private Object data;

}
