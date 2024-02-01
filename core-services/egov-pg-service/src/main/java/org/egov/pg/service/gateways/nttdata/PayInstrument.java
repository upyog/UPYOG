package org.egov.pg.service.gateways.nttdata;

import java.util.List;

public class PayInstrument {

	public SettlementDetails getSettlementDetails() {
		return settlementDetails;
	}
	public void setSettlementDetails(SettlementDetails settlementDetails) {
		this.settlementDetails = settlementDetails;
	}
	private HeadDetails headDetails;
	private SettlementDetails settlementDetails;
	private MerchDetails merchDetails;
	private PayDetails payDetails;
	private ResponseUrls responseUrls;
	private PayModeSpecificData payModeSpecificData;
	private Extras extras;
	private CustDetails custDetails;
	public ResponseDetails responseDetails;
	public ResponseDetails getResponseDetails() {
		return responseDetails;
	}
//	public List<responseDeatails> getResponseDeatil() {
//		return responseDeatil;
//	}
//	public void setResponseDeatil(List<responseDeatails> responseDeatil) {
//		this.responseDeatil = responseDeatil;
//	}
	public void setResponseDetails(ResponseDetails responseDetails) {
		this.responseDetails = responseDetails;
	}
	
	public HeadDetails getHeadDetails() {
		return headDetails;
	}
	public void setHeadDetails(HeadDetails headDetails) {
		this.headDetails = headDetails;
	}
	public MerchDetails getMerchDetails() {
		return merchDetails;
	}
	public void setMerchDetails(MerchDetails merchDetails) {
		this.merchDetails = merchDetails;
	}
	public PayDetails getPayDetails() {
		return payDetails;
	}
	public void setPayDetails(PayDetails payDetails) {
		this.payDetails = payDetails;
	}
	public ResponseUrls getResponseUrls() {
		return responseUrls;
	}
	public void setResponseUrls(ResponseUrls responseUrls) {
		this.responseUrls = responseUrls;
	}
	public PayModeSpecificData getPayModeSpecificData() {
		return payModeSpecificData;
	}
	public void setPayModeSpecificData(PayModeSpecificData payModeSpecificData) {
		this.payModeSpecificData = payModeSpecificData;
	}
	public Extras getExtras() {
		return extras;
	}
	public void setExtras(Extras extras) {
		this.extras = extras;
	}
	public CustDetails getCustDetails() {
		return custDetails;
	}
	public void setCustDetails(CustDetails custDetails) {
		this.custDetails = custDetails;
	}
	@Override
	public String toString() {
		return "PayInstrument [headDetails=" + headDetails + ", merchDetails=" + merchDetails + ", settlementDetails=" + settlementDetails
				+ ", payDetails="
				+ payDetails + ", responseUrls=" + responseUrls + ", payModeSpecificData=" + payModeSpecificData
				+ ", extras=" + extras + ", custDetails=" + custDetails + ", responseDetails=" + responseDetails
				+ ", getResponseDetails()=" + getResponseDetails() + ", getHeadDetails()=" + getHeadDetails()
				+ ", getMerchDetails()=" + getMerchDetails() + ", getPayDetails()=" + getPayDetails()
				+ ", getResponseUrls()=" + getResponseUrls() + ", getPayModeSpecificData()=" + getPayModeSpecificData()
				+ ", getExtras()=" + getExtras() + ", getCustDetails()=" + getCustDetails() + ", getSettlementDetails()=" + getSettlementDetails() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	
	
}
