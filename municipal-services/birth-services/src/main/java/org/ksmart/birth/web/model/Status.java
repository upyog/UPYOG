package org.ksmart.birth.web.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
	ACTIVE("ACTIVE"), INACTIVE("INACTIVE"), CANCELLED("CANCELLED");

	private String value;

	Status(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
    public String toString() {
        return name();
    }

	@JsonCreator
	public static Status fromValue(String passedValue) {
		for (Status obj : Status.values()) {
			if (String.valueOf(obj.value).equals(passedValue.toUpperCase())) {
				return obj;
			}
		}
		return null;
	}
}
