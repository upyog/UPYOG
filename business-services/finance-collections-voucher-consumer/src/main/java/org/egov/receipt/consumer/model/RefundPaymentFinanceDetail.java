package org.egov.receipt.consumer.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundPaymentFinanceDetail {

	private String refundApplicationNumber;
	private String gatewayRefundId;
	private BigDecimal refundAmount;
	private String refundMode;
	private String paymentStatus;
	private String payableGlCode;
	private String bankAccountNumber;
	private String fundCode;
	private String departmentCode;
	private String functionCode;
}