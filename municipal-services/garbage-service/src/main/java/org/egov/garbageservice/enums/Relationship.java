package org.egov.garbageservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Guardian relationship type on property owner records.
 * <p>
 * Behavior:
 * - Values: FATHER, HUSBAND (legacy property/PT contract values).
 * - Serialized as string value via {@link #toString()} and {@link JsonValue}.
 * - Parsed from JSON with {@link #fromValue(String)}.
 * <p>
 * Notes:
 * - Used on {@link org.egov.garbageservice.web.models.contract.OwnerInfo}.
 * - For user-service guardian field use {@link GuardianRelation} on {@link org.egov.garbageservice.web.models.UserV2}.
 */
public enum Relationship {

    FATHER("FATHER"), HUSBAND("HUSBAND");

    private String value;

    /**
     * Executes toString utility operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates input parameters.</li>
     *   <li>Executes helper business logic or transformation.</li>
     *   <li>Returns formatted output result.</li>
     * </ol>
     *
     * @return the output result
     */

    Relationship(String value) {
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
    public static Relationship fromValue(String text) {
        for (Relationship b : Relationship.values()) {
            if (String.valueOf(b.value).equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Executes toString utility operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates input parameters.</li>
     *   <li>Executes helper business logic or transformation.</li>
     *   <li>Returns formatted output result.</li>
     * </ol>
     *
     * @return the output result
     */

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}
