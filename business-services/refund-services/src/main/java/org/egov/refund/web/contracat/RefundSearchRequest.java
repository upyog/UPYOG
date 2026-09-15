package org.egov.refund.web.contracat;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundSearchRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	
    private String tenantId;

    private String moduleName;

    private String businessService;

    private String consumerCode;

    private String paymentId;

    private String refundNo;

    private String status;

    private String refundCategory;

    private String gatewayRefundId;

    private String sanctionRef;
}