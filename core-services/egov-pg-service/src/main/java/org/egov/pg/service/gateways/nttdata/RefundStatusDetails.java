package org.egov.pg.service.gateways.nttdata;

import java.util.List;

public class RefundStatusDetails {

	private List<RefundDetails> refundDetails;
	
	public List<RefundDetails> getRefundDetails() {
		return refundDetails;
	}
	
	public void setRefundDetails(List<RefundDetails> refundDetails) {
		this.refundDetails = refundDetails;
	}
	
	@Override
	public String toString() {
		return "RefundStatusDetails [refundDetails=" + refundDetails + "]";
	}
}
