package org.egov.egf.contract.model;

import java.util.ArrayList;
import java.util.List;

import org.egov.infra.microservice.models.ResponseInfo;
import org.egov.model.refund.RefundApplication;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundApplicationResponse {

	@JsonProperty("RefundApplications")
	private List<RefundApplication> refundApplications = new ArrayList<>(0);

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	public List<RefundApplication> getRefundApplications() {

		return refundApplications;
	}

	public void setRefundApplications(final List<RefundApplication> refundApplications) {

		this.refundApplications = refundApplications;
	}

	public ResponseInfo getResponseInfo() {
		return responseInfo;
	}

	public void setResponseInfo(final ResponseInfo responseInfo) {

		this.responseInfo = responseInfo;
	}
}