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
public class RefundDetail {

	private String id;

	private String tenantId;

	/*
	 * Identifies the module producing the refund request. Examples: CHB, TL, PT.
	 */
	private String moduleName;

	/*
	 * Finance business-service code used to resolve account and business-service
	 * configuration. Example: CHB.REFUND.
	 */
	private String businessService;

	/*
	 * Unique refund reference used for duplicate checking and as the Finance
	 * voucher reference document.
	 */
	private String refundApplicationNumber;

	/*
	 * Original booking, licence, property, receipt, consumer or other source-module
	 * reference.
	 */
	private String referenceNumber;

	/*
	 * Original payment reference.
	 */
	private String paymentId;

	/*
	 * Original receipt reference, when available.
	 */
	private String receiptNumber;

	private BigDecimal refundAmount;

	private String refundReason;

	private Long refundDate;

	private String status;

	/*
	 * Earlier temporary design used these fields directly from the refund request.
	 *
	 * Remove them later when GL codes are fetched from MDMS.
	 */
	private String debitGlCode;

	private String creditGlCode;

	private String fundCode;

	private String departmentCode;

	private String functionCode;
}