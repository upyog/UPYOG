package org.egov.garbageservice.model;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
/**
 * Simple request carrying a billing service bill id for tracker lookup or maintenance jobs.
 * Used by scheduler extract-tracker endpoint.
 */
@AllArgsConstructor
public class BillIdRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("billId")
	@CustomSafeHtml
	private String billId;
}
