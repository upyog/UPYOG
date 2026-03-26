package org.egov.pg.service.gateways.nttdata;

public class RefundStatus {
	
	private String refundTxnId;
	private String refundAmt;
	private String refundInitiatedDate;
	private String remarks;
	private String prodRefundId;
	public String getRefundTxnId() {
		return refundTxnId;
	}
	public void setRefundTxnId(String refundTxnId) {
		this.refundTxnId = refundTxnId;
	}

	public String getRefundAmt() {
		return refundAmt;
	}
	public void setRefundAmt(String refundAmt) {
		this.refundAmt = refundAmt;
	}
	public String getRefundInitiatedDate() {
		return refundInitiatedDate;
	}
	public void setRefundInitiatedDate(String refundInitiatedDate) {
		this.refundInitiatedDate = refundInitiatedDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getProdRefundId() {
		return prodRefundId;
	}
	public void setProdRefundId(String prodRefundId) {
		this.prodRefundId = prodRefundId;
	}
	
	@Override
	public String toString() {
		return "RefundStatus [refundTxnId=" + refundTxnId + ", refundAmt=" + refundAmt + ", refundInitiatedDate="
				+ refundInitiatedDate + ", remarks=" + remarks + ", prodRefundId=" + prodRefundId + "]";
	}

}
