package org.egov.receipt.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundPaymentMapping {

	private String tenantId;
	private String businessService;
	private String refundMode;

	// Used while creating the refund JV
	private String debitGlCode;

	// JV credit GL and Payment Voucher debit GL
	private String payableGlCode;

	private String fund;
	private String department;
	private String function;

	// Used while creating the Payment Voucher
	private String bankAccountNumber;
	private String paymentMode;

	private Boolean isActive;
}