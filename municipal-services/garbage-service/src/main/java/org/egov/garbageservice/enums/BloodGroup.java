package org.egov.garbageservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

/**
 * Blood group options for user demographic data.
 *
 * Behavior:
 * - Constants map to display values (A+, B+, O+, AB+, and negative groups) via {@code value} field.
 * - {@link #fromValue(String)} accepts either the display value or the enum constant name (case-insensitive).
 * - Exposed through Lombok {@code @Getter} on the value field.
 *
 * Notes:
 * - Used on {@link org.egov.garbageservice.model.UserV2} when syncing with user service.
 * - Unknown API values deserialize to null from fromValue.
 */
@Getter
public enum BloodGroup {
	A_POSITIVE("A+"), B_POSITIVE("B+"), O_POSITIVE("O+"), AB_POSITIVE("AB+"), A_NEGATIVE("A-"), B_NEGATIVE("B-"),
	AB_NEGATIVE("AB-"), O_NEGATIVE("O-");

	private String value;

	BloodGroup(String value) {
		this.value = value;
	}

	@JsonCreator
	public static BloodGroup fromValue(String text) {
		for (BloodGroup b : BloodGroup.values()) {
			if (String.valueOf(b.value).equalsIgnoreCase(text) || String.valueOf(b.name()).equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
