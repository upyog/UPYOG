package org.egov.garbageservice.model.contract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * High-level outcome of an API call as reported in ResponseInfo.
 * Values are SUCCESSFUL or FAILED; serialized to JSON as the string value.
 * May be extended in future to include in-progress states.
 */
public enum ResponseStatusEnum {
	SUCCESSFUL("SUCCESSFUL"),

	FAILED("FAILED");

	private String value;

	ResponseStatusEnum(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static ResponseStatusEnum fromValue(String text) {
		for (ResponseStatusEnum b : ResponseStatusEnum.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
}