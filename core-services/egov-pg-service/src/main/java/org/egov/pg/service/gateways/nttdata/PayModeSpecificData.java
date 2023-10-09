package org.egov.pg.service.gateways.nttdata;

import java.util.Arrays;

public class PayModeSpecificData {
	private String[] subChannel;
	private BankDetails bankDetails;
	public String[] getSubChannel() {
		return subChannel;
	}
	public void setSubChannel(String[] subChannel) {
		this.subChannel = subChannel;
	}
	public BankDetails getBankDetails() {
		return bankDetails;
	}
	public void setBankDetails(BankDetails bankDetails) {
		this.bankDetails = bankDetails;
	}
	@Override
	public String toString() {
		return "PayModeSpecificData [subChannel=" + Arrays.toString(subChannel) + ", bankDetails=" + bankDetails
				+ ", getSubChannel()=" + Arrays.toString(getSubChannel()) + ", getBankDetails()=" + getBankDetails()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
}
