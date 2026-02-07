package org.egov.pg.service.gateways.nttdata;

import java.util.List;



public class PayDetails {

	
	
	//private List<ProdDetails> prodDetails;
	private String product;
	private String amount;
	private String	surchargeAmount;
	private String totalAmount;
	private String custAccNo;
	private String custAccIfsc;
	private String clientCode;
	private String txnCurrency;
	private String signature;
	private String atomTxnId;
	private String totalRefundAmount;
	private String txnInitDate;
	private String txnCompleteDate;
	public String getTxnInitDate() {
		return txnInitDate;
	}
	public void setTxnInitDate(String txnInitDate) {
		this.txnInitDate = txnInitDate;
	}
	public String getTxnCompleteDate() {
		return txnCompleteDate;
	}
	public void setTxnCompleteDate(String txnCompleteDate) {
		this.txnCompleteDate = txnCompleteDate;
	}
	public String getAtomTxnId() {
		return atomTxnId;
	}
	public void setAtomTxnId(String atomTxnId) {
		this.atomTxnId = atomTxnId;
	}
	public String getTotalRefundAmount() {
		return totalRefundAmount;
	}
	public void setTotalRefundAmount(String totalRefundAmount) {
		this.totalRefundAmount = totalRefundAmount;
	}

	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSurchargeAmount() {
		return surchargeAmount;
	}
	public void setSurchargeAmount(String surchargeAmount) {
		this.surchargeAmount = surchargeAmount;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getCustAccNo() {
		return custAccNo;
	}
	public void setCustAccNo(String custAccNo) {
		this.custAccNo = custAccNo;
	}
	public String getCustAccIfsc() {
		return custAccIfsc;
	}
	public void setCustAccIfsc(String custAccIfsc) {
		this.custAccIfsc = custAccIfsc;
	}
	public String getClientCode() {
		return clientCode;
	}
	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}
	public String getTxnCurrency() {
		return txnCurrency;
	}
	public void setTxnCurrency(String txnCurrency) {
		this.txnCurrency = txnCurrency;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	@Override
	public String toString() {
		return "PayDetails [product=" + product + ", amount=" + amount + ", surchargeAmount=" + surchargeAmount
				+ ", totalAmount=" + totalAmount + ", custAccNo=" + custAccNo + ", custAccIfsc=" + custAccIfsc
				+ ", clientCode=" + clientCode + ", txnCurrency=" + txnCurrency + ", signature=" + signature
				+ ", atomTxnId=" + atomTxnId + ", totalRefundAmount=" + totalRefundAmount + ", txnInitDate="
				+ txnInitDate + ", txnCompleteDate=" + txnCompleteDate + ", getTxnInitDate()=" + getTxnInitDate()
				+ ", getTxnCompleteDate()=" + getTxnCompleteDate() + ", getAtomTxnId()=" + getAtomTxnId()
				+ ", getTotalRefundAmount()=" + getTotalRefundAmount() + ", getProduct()=" + getProduct()
				+ ", getAmount()=" + getAmount() + ", getSurchargeAmount()=" + getSurchargeAmount()
				+ ", getTotalAmount()=" + getTotalAmount() + ", getCustAccNo()=" + getCustAccNo()
				+ ", getCustAccIfsc()=" + getCustAccIfsc() + ", getClientCode()=" + getClientCode()
				+ ", getTxnCurrency()=" + getTxnCurrency() + ", getSignature()=" + getSignature() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}

}
