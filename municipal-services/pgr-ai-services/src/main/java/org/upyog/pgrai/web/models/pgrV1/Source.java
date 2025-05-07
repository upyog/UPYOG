package org.upyog.pgrai.web.models.pgrV1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the source of a service request in the system.
 * This enumeration defines the possible sources:
 * - WEBAPP: Represents requests originating from the web application.
 * - MOBILEAPP: Represents requests originating from the mobile application.
 *
 * This enum is part of the PGR V1 module and is used to manage
 * source-related information for service requests.
 */
public enum Source {
	WEBAPP("WEBAPP"), MOBILEAPP("MOBILEAPP");

	private String value;

	Source(String value) {
		this.value = value;
	}

	/**
	 * Returns the name of the enum constant as a string.
	 *
	 * @return the name of the enum constant
	 */
	@Override
	@JsonValue
	public String toString() {
		return name();
	}

	/**
	 * Converts a given string value to the corresponding Source enum constant.
	 *
	 * @param passedValue the string value to convert
	 * @return the corresponding Source enum constant, or null if no match is found
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
