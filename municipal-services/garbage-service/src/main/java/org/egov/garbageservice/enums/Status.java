package org.egov.garbageservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Lifecycle status for property owner and document records in garbage/property contracts.
 *
 * Behavior:
 * - Covers active/inactive, workflow states (INWORKFLOW, PENDINGFOR*, INITIATED), and terminal states
 *   (APPROVED, REJECTED, CANCELLED).
 * - Serialized to JSON as the string value via {@link #toString()} and {@link JsonValue}.
 * - Parsed from API text with {@link #fromValue(String)} (case-insensitive).
 *
 * Notes:
 * - Used on {@link org.egov.garbageservice.model.contract.OwnerInfo} and
 *   {@link org.egov.garbageservice.model.contract.Document}.
 * - Distinct from garbage account application status (workflow) and {@link org.egov.garbageservice.contract.bill.Bill.StatusEnum}.
 */
public enum Status {

	ACTIVE("ACTIVE"),

	INACTIVE("INACTIVE"),

	INWORKFLOW("INWORKFLOW"),

	CANCELLED("CANCELLED"),

	REJECTED("REJECTED"),

	INITIATED("INITIATED"),

	PENDINGFORVERIFICATION("PENDINGFORVERIFICATION"),

	PENDINGFORMODIFICATION("PENDINGFORMODIFICATION"),

	PENDINGFORAPPROVAL("PENDINGFORAPPROVAL"),

	APPROVED("APPROVED");

	private String value;

	Status(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static Status fromValue(String text) {
		for (Status b : Status.values()) {
			if (String.valueOf(b.value).equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
