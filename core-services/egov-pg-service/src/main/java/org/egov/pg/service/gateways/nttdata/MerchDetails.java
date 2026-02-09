package org.egov.pg.service.gateways.nttdata;

public class MerchDetails {
	private String merchId;
	private String	userId;
	private String password;
	private String merchTxnId;
	private String merchType;
	private String mccCode;
	private String merchTxnDate;
	
	public String getMerchId() {
		return merchId;
	}
	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMerchTxnId() {
		return merchTxnId;
	}
	public void setMerchTxnId(String merchTxnId) {
		this.merchTxnId = merchTxnId;
	}
	public String getMerchType() {
		return merchType;
	}
	public void setMerchType(String merchType) {
		this.merchType = merchType;
	}
	public String getMccCode() {
		return mccCode;
	}
	public void setMccCode(String mccCode) {
		this.mccCode = mccCode;
	}
	public String getMerchTxnDate() {
		return merchTxnDate;
	}
	public void setMerchTxnDate(String merchTxnDate) {
		this.merchTxnDate = merchTxnDate;
	}
	@Override
	public String toString() {
		return "MerchDetails [merchId=" + merchId + ", userId=" + userId + ", password=" + password + ", merchTxnId="
				+ merchTxnId + ", merchType=" + merchType + ", mccCode=" + mccCode + ", merchTxnDate=" + merchTxnDate
				+ ", getMerchId()=" + getMerchId() + ", getUserId()=" + getUserId() + ", getPassword()=" + getPassword()
				+ ", getMerchTxnId()=" + getMerchTxnId() + ", getMerchType()=" + getMerchType() + ", getMccCode()="
				+ getMccCode() + ", getMerchTxnDate()=" + getMerchTxnDate() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
