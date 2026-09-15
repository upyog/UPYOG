package org.egov.egf.contract.model;

public class RefundPaymentResponseDetail {

	private String refundApplicationNumber;

	private String journalVoucherNumber;

	private String paymentVoucherNumber;

	private String gatewayRefundId;

	private String paymentStatus;

	public String getRefundApplicationNumber() {
		return refundApplicationNumber;
	}

	public void setRefundApplicationNumber(final String refundApplicationNumber) {

		this.refundApplicationNumber = refundApplicationNumber;
	}

	public String getJournalVoucherNumber() {
		return journalVoucherNumber;
	}

	public void setJournalVoucherNumber(final String journalVoucherNumber) {

		this.journalVoucherNumber = journalVoucherNumber;
	}

	public String getPaymentVoucherNumber() {
		return paymentVoucherNumber;
	}

	public void setPaymentVoucherNumber(final String paymentVoucherNumber) {

		this.paymentVoucherNumber = paymentVoucherNumber;
	}

	public String getGatewayRefundId() {
		return gatewayRefundId;
	}

	public void setGatewayRefundId(final String gatewayRefundId) {

		this.gatewayRefundId = gatewayRefundId;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(final String paymentStatus) {

		this.paymentStatus = paymentStatus;
	}
}