package org.egov.receipt.consumer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundPaymentFinanceRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	private String tenantId;

	@JsonProperty("RefundPayment")
	private RefundPaymentFinanceDetail refundPayment;
}