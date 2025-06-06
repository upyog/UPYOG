package org.egov.pt.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BillStatus {

	ACTIVE("ACTIVE"),

	CANCELLED("CANCELLED"),

	PAID("PAID"),

	PARTIALLY_PAID("PARTIALLY_PAID"),

	PAYMENT_CANCELLED("PAYMENT_CANCELLED"),

	EXPIRED("EXPIRED");

	private String value;

	BillStatus(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static BillStatus fromValue(String text) {
		for (BillStatus b : BillStatus.values()) {
			if (String.valueOf(b.value).equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
