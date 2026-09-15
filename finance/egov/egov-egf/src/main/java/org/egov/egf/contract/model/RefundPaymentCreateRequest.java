package org.egov.egf.contract.model;

import org.egov.infra.microservice.models.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundPaymentCreateRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	private String tenantId;

	@JsonProperty("RefundPayment")
	private RefundPaymentDetail refundPayment;

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public void setRequestInfo(final RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(final String tenantId) {
		this.tenantId = tenantId;
	}

	public RefundPaymentDetail getRefundPayment() {
		return refundPayment;
	}

	public void setRefundPayment(final RefundPaymentDetail refundPayment) {

		this.refundPayment = refundPayment;
	}
}