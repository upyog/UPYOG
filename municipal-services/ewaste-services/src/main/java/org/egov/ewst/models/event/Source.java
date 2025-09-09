package org.egov.ewst.models.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the source of an event in the Ewaste application.
 * This enum contains the possible sources such as WEBAPP and MOBILEAPP.
 */
public enum Source {
	WEBAPP("WEBAPP"), MOBILEAPP("MOBILEAPP");

	// Value associated with the source
	private String value;

	// Constructor to set the value of the source
	Source(String value) {
		this.value = value;
	}

	/**
	 * Returns the string representation of the source.
	 *
	 * @return the name of the source
	 */
	@Override
	@JsonValue
	public String toString() {
		return name();
	}

	/**
	 * Creates a Source enum from a given string value.
	 *
	 * @param passedValue the string value to convert
	 * @return the corresponding Source enum, or null if no match is found
	 */
	@JsonCreator
	public static Source fromValue(String passedValue) {
		for (Source obj : Source.values()) {
			if (String.valueOf(obj.value).equals(passedValue.toUpperCase())) {
				return obj;
			}
		}
		return null;
	}
}