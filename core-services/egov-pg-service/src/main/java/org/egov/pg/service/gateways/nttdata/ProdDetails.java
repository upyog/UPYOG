package org.egov.pg.service.gateways.nttdata;

public class ProdDetails {
	private String prodName ;
	private String prodAmount;
	private Double prodRefundAmount;
	private String prodRefundId;
	public Double getProdRefundAmount() {
		return prodRefundAmount;
	}
	public void setProdRefundAmount(Double prodRefundAmount) {
		this.prodRefundAmount = prodRefundAmount;
	}
	public String getProdRefundId() {
		return prodRefundId;
	}
	public void setProdRefundId(String prodRefundId) {
		this.prodRefundId = prodRefundId;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public String getProdAmount() {
		return prodAmount;
	}
	public void setProdAmount(String prodAmount) {
		this.prodAmount = prodAmount;
	}
	@Override
	public String toString() {
		return "ProdDetails [prodName=" + prodName + ", prodAmount=" + prodAmount + ", prodRefundAmount="
				+ prodRefundAmount + ", prodRefundId=" + prodRefundId + ", getProdRefundAmount()="
				+ getProdRefundAmount() + ", getProdRefundId()=" + getProdRefundId() + ", getProdName()="
				+ getProdName() + ", getProdAmount()=" + getProdAmount() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
