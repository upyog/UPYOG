package org.egov.garbageservice.model;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.annotations.CustomSafeHtml;
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
/**
 * Request for dashboard-style aggregate counts of garbage applications by status.
 * Posted to /garbage-accounts/_counts with tenantId and RequestInfo.
 */
@Builder
public class TotalCountRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@CustomSafeHtml
	private String tenantId;

}
