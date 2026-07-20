package org.egov.garbageservice.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
/**
 * On-demand bill generation for specific accounts with optional override bill amount.
 * Wraps GenerateBillRequest plus RequestInfo for the scheduler on-demand endpoint.
 */
@AllArgsConstructor
public class OnDemandBillRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@JsonProperty("generateBillRequest")
	private GenerateBillRequest generateBillRequest;
	
	@JsonProperty("billAmount")
	private BigDecimal billAmount;

}
