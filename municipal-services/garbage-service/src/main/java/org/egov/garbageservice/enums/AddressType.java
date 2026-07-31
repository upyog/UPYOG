package org.egov.garbageservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Classification of an address on user or property-linked records.
 * <p>
 * Behavior:
 * - PERMANENT and CORRESPONDENCE are primary types; additional constants (USUALADDRESS, EVENTADDRESS, etc.)
 * map to the CORRESPONDENCE string value for backward compatibility.
 * - Parsed from JSON via {@link #fromValue(String)} (exact match on stored value string).
 * - Used on {@link org.egov.garbageservice.model.Address}.
 * <p>
 * Notes:
 * - Constant declaration order must not change — see in-file comment (legacy ordinal usage).
 * - Several distinct enum names share the same {@code value} string; fromValue resolves by that string only.
 */
public enum AddressType {
    // This order should not be interrupted
    PERMANENT("PERMANENT"), CORRESPONDENCE("CORRESPONDENCE"), USUALADDRESS("CORRESPONDENCE"),
    EVENTADDRESS("CORRESPONDENCE"), PRESENTADDRESS("CORRESPONDENCE"), PROPERTYADDRESS("CORRESPONDENCE");

    private String value;

    /**
     * Constructs a new instance of AddressType.
     *
     * @param value string value associated with this enum constant
     */

    AddressType(String value) {
        this.value = value;
    }

    /**
     * Executes fromValue utility operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates input parameters.</li>
     *   <li>Executes helper business logic or transformation.</li>
     *   <li>Returns formatted output result.</li>
     * </ol>
     *
     * @param text the string value to process or resolve
     * @return the output result
     */

    @JsonCreator
    public static AddressType fromValue(String text) {
        for (AddressType b : AddressType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

}
