package org.egov.egf.contract.model;

import java.math.BigDecimal;

public class RefundPaymentDetail {

	private String refundApplicationNumber;

	private String gatewayRefundId;

	private String gatewayTransactionId;

	private Long gatewayTransactionDate;

	private BigDecimal refundAmount;

	private String refundMode;

	private String paymentStatus;

	private String paymentGateway;

	/*
	 * Finance configuration resolved by the consumer from MDMS.
	 */
	private String payableGlCode;

	private String bankAccountNumber;

	private String fundCode;

	private String departmentCode;

	private String functionCode;

	public String getRefundApplicationNumber() {
		return refundApplicationNumber;
	}

	public void setRefundApplicationNumber(final String refundApplicationNumber) {

		this.refundApplicationNumber = refundApplicationNumber;
	}

	public String getGatewayRefundId() {
		return gatewayRefundId;
	}

	public void setGatewayRefundId(final String gatewayRefundId) {

		this.gatewayRefundId = gatewayRefundId;
	}

	public String getGatewayTransactionId() {
		return gatewayTransactionId;
	}

	public void setGatewayTransactionId(final String gatewayTransactionId) {

		this.gatewayTransactionId = gatewayTransactionId;
	}

	public Long getGatewayTransactionDate() {
		return gatewayTransactionDate;
	}

	public void setGatewayTransactionDate(final Long gatewayTransactionDate) {

		this.gatewayTransactionDate = gatewayTransactionDate;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(final BigDecimal refundAmount) {

		this.refundAmount = refundAmount;
	}

	public String getRefundMode() {
		return refundMode;
	}

	public void setRefundMode(final String refundMode) {
		this.refundMode = refundMode;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(final String paymentStatus) {

		this.paymentStatus = paymentStatus;
	}

	public String getPaymentGateway() {
		return paymentGateway;
	}

	public void setPaymentGateway(final String paymentGateway) {

		this.paymentGateway = paymentGateway;
	}

	public String getPayableGlCode() {
		return payableGlCode;
	}

	public void setPayableGlCode(final String payableGlCode) {

		this.payableGlCode = payableGlCode;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(final String bankAccountNumber) {

		this.bankAccountNumber = bankAccountNumber;
	}

	public String getFundCode() {
		return fundCode;
	}

	public void setFundCode(final String fundCode) {
		this.fundCode = fundCode;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(final String departmentCode) {

		this.departmentCode = departmentCode;
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(final String functionCode) {

		this.functionCode = functionCode;
	}
}