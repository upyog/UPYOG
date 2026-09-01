package org.egov.egf.contract.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.infra.microservice.models.RequestInfo;
import org.egov.model.refund.RefundApplication;
import org.hibernate.validator.constraints.SafeHtml;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundApplicationRequest {

	@NotNull
	@JsonProperty("tenantId")
	@SafeHtml
	private String tenantId;

	@NotNull
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@NotNull
	@JsonProperty("Refund")
	private RefundApplication refundApplication;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(final String tenantId) {

		this.tenantId = tenantId;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public void setRequestInfo(final RequestInfo requestInfo) {

		this.requestInfo = requestInfo;
	}

	public RefundApplication getRefundApplication() {
		return refundApplication;
	}

	public void setRefundApplication(final RefundApplication refundApplication) {

		this.refundApplication = refundApplication;
	}
}