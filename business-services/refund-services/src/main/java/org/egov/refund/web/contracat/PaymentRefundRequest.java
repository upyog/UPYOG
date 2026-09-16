package org.egov.refund.web.contracat;

import org.egov.common.contract.request.RequestInfo;
import org.egov.refund.model.PaymentRefund;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRefundRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("Refund")
	private PaymentRefund refund;
	
}