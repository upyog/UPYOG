package org.egov.finance.voucher.enumeration;

public enum CloseTypeEnum {

	SOFTCLOSE("Soft Close"), HARDCLOSE("Hard Close");

	private String value;

	CloseTypeEnum(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
