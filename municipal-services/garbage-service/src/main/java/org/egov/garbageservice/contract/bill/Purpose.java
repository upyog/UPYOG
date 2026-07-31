package org.egov.garbageservice.contract.bill;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum for classifying a {@link BillAccountDetail} line by charge type or period.
 * <p>
 * Behavior:
 * - Values include CURRENT, ARREAR, ADVANCE, penalty/rebate types, and OTHERS.
 * - Serialized to JSON as the enum string value via {@link #toString()} and {@link JsonValue}.
 * - Deserialized from API text with {@link #fromValue(String)}.
 * <p>
 * Notes:
 * - Must match purpose codes accepted by the billing/collection service.
 * - Used on bill account lines, not on {@link Demand} headers directly.
 */
public enum Purpose {

    ARREAR("ARREAR"),

    CURRENT("CURRENT"),

    ADVANCE("ADVANCE"),

    EXEMPTION("EXEMPTION"),

    ARREAR_LATEPAYMENT_CHARGES("ARREAR_LATEPAYMENT_CHARGES"),

    CURRENT_LATEPAYMENT_CHARGES("CURRENT_LATEPAYMENT_CHARGES"),

    CHEQUE_BOUNCE_PENALTY("CHEQUE_BOUNCE_PENALTY"),

    REBATE("REBATE"),

    OTHERS("OTHERS");

    private String value;

    /**
     * Constructs the Purpose enum with the specified string value.
     *
     * @param value the string representation of the purpose
     */

    Purpose(String value) {
        this.value = value;
    }

    /**
     * Resolves a string value to its corresponding {@link Purpose} constant during JSON deserialization.
     *
     * @param text the string value representing the purpose
     * @return the matching {@link Purpose}, or null if no match is found
     */

    @JsonCreator
    public static Purpose fromValue(String text) {
        for (Purpose b : Purpose.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Returns the string representation of the purpose.
     *
     * @return the purpose value as a string
     */

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}