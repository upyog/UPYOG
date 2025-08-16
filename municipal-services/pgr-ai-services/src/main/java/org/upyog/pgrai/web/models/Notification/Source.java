package org.upyog.pgrai.web.models.Notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the source of a notification in the system.
 * This enum defines the possible sources from which a notification can originate, including:
 * - WEBAPP: Notifications originating from the web application.
 * - MOBILEAPP: Notifications originating from the mobile application.
 *
 * This enum is part of the notification module in the PGR system.
 */
public enum Source {
	WEBAPP("WEBAPP"),
	MOBILEAPP("MOBILEAPP");

	private String value;

	Source(String value) {
		this.value = value;
	}

	/**
	 * Returns the string representation of the source.
	 *
	 * @return the name of the source.
	 */
	@Override
	@JsonValue
	public String toString() {
		return name();
	}

	/**
	 * Creates a Source enum from the given string value.
	 *
	 * @param passedValue the string value representing the source.
	 * @return the corresponding Source enum, or null if no match is found.
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
