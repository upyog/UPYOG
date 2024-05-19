package org.egov.swcalculation.web.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * status of the Bill
 */
public enum BillStatus {

	INITIATED("INITIATED"), INPROGRESS("INPROGRESS"), COMPLETED("COMPLETED");

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
