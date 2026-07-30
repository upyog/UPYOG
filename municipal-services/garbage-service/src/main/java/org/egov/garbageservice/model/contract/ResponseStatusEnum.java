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

    /**
     * Constructs the enum with the specified status value.
     *
     * @param value the string representation of the response status
     */

    ResponseStatusEnum(String value) {
        this.value = value;
    }

    /**
     * Resolves a string value to its corresponding {@link ResponseStatusEnum} constant during JSON deserialization.
     *
     * @param text the string value representing the status
     * @return the matching {@link ResponseStatusEnum}, or null if no match is found
     */

    @JsonCreator
    public static ResponseStatusEnum fromValue(String text) {
        for (ResponseStatusEnum b : ResponseStatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Returns the string representation of the response status.
     *
     * @return the response status value as a string
     */

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}