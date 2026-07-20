package org.egov.garbageservice.model;

import java.util.List;

import org.egov.tracer.annotations.CustomSafeHtml;
import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
/**
 * API response listing GrbgBillTracker rows and standard ResponseInfo metadata.
 * Returned when searching or updating bill tracker state after generation runs.
 */
@AllArgsConstructor
public class GrbgBillTrackerResponse {

	@JsonProperty("responseInfo")
	ResponseInfo responseInfo;

	@JsonProperty("grbgBillTrackers")
	private List<GrbgBillTracker> grbgBillTrackers;
	
	@JsonProperty("message")
	@CustomSafeHtml
	private String message;

}
