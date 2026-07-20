package org.egov.garbageservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Guardian relationship type on property owner records.
 *
 * Behavior:
 * - Values: FATHER, HUSBAND (legacy property/PT contract values).
 * - Serialized as string value via {@link #toString()} and {@link JsonValue}.
 * - Parsed from JSON with {@link #fromValue(String)}.
 *
 * Notes:
 * - Used on {@link org.egov.garbageservice.model.contract.OwnerInfo}.
 * - For user-service guardian field use {@link GuardianRelation} on {@link org.egov.garbageservice.model.UserV2}.
 */
public enum Relationship {

	FATHER("FATHER"), HUSBAND("HUSBAND");

	private String value;

	Relationship(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static Relationship fromValue(String text) {
		for (Relationship b : Relationship.values()) {
			if (String.valueOf(b.value).equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
