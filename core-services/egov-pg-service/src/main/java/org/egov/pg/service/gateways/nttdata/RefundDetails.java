package org.egov.pg.service.gateways.nttdata;

import java.util.List;

public class RefundDetails {
 
	private String prodName;
	private List<RefundStatus> refundStatus;
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public List<RefundStatus> getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(List<RefundStatus> refundStatus) {
		this.refundStatus = refundStatus;
	}
	@Override
	public String toString() {
		return "RefundDetails [prodName=" + prodName + ", refundStatus=" + refundStatus + "]";
	}
}
