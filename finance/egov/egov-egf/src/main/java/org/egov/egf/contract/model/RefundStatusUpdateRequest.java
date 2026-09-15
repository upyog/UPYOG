package org.egov.egf.contract.model;

import org.egov.infra.microservice.models.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundStatusUpdateRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	private RefundStatusUpdate refund;

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public void setRequestInfo(final RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	public RefundStatusUpdate getRefund() {
		return refund;
	}

	public void setRefund(final RefundStatusUpdate refund) {
		this.refund = refund;
	}
}