package org.egov.ewst.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * status of the Ewaste Application
 */
public enum Status {


	NEWREQUEST("REQUESTCREATED"),
	
	PRODUCTVERIFIED("PRODUCTVERIFIED"),

	COMPLETIONPENDING("COMPLETIONPENDING"),

	REJECTED("REQUESTREJECTED"),

	REQUESTCOMPLETED("REQUESTCOMPLETED");

	private String value;

	Status(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static Status fromValue(String text) {
		for (Status b : Status.values()) {
			if (String.valueOf(b.value).equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
