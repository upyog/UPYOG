package org.egov.pg.service.gateways.nttdata;

public class BankDetails {
	private String otsBankId;
	private String bankTxnId;
	private String otsBankName;
	private String cardType;
	private String cardMaskNumber;

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardMaskNumber() {
		return cardMaskNumber;
	}

	public void setCardMaskNumber(String cardMaskNumber) {
		this.cardMaskNumber = cardMaskNumber;
	}

	public String getOtsBankId() {
		return otsBankId;
	}

	public void setOtsBankId(String otsBankId) {
		this.otsBankId = otsBankId;
	}

	public String getBankTxnId() {
		return bankTxnId;
	}

	public void setBankTxnId(String bankTxnId) {
		this.bankTxnId = bankTxnId;
	}

	public String getOtsBankName() {
		return otsBankName;
	}

	public void setOtsBankName(String otsBankName) {
		this.otsBankName = otsBankName;
	}

	@Override
	public String toString() {
		return "BankDetails [otsBankId=" + otsBankId + ", bankTxnId=" + bankTxnId + ", otsBankName=" + otsBankName + ", cardType=" + cardType + ", cardMaskNumber=" + cardMaskNumber 
				+ ", getOtsBankId()=" + getOtsBankId() + ", getBankTxnId()=" + getBankTxnId() + ", getOtsBankName()="
				+ getOtsBankName() + ", getCardType()=" + getCardType() + ", getCardMaskNumber()=" + getCardMaskNumber()+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
