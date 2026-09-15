package org.egov.egf.contract.model;

import org.egov.infra.microservice.models.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundPaymentCreateResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("RefundPayment")
	private RefundPaymentResponseDetail refundPayment;

	public ResponseInfo getResponseInfo() {
		return responseInfo;
	}

	public void setResponseInfo(final ResponseInfo responseInfo) {

		this.responseInfo = responseInfo;
	}

	public RefundPaymentResponseDetail getRefundPayment() {
		return refundPayment;
	}

	public void setRefundPayment(final RefundPaymentResponseDetail refundPayment) {

		this.refundPayment = refundPayment;
	}
}