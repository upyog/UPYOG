package org.egov.collection.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class PaymentRefund {

	private String tenantId;

	private String refundId;

	private String transactionId;

	private String refundStatus;
	
	private String gatewayStausMsg;
	
	private String gatewayStatusCode;
}