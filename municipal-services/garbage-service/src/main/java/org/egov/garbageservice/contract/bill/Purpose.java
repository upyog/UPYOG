package org.egov.garbageservice.contract.bill;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum for classifying a {@link BillAccountDetail} line by charge type or period.
 *
 * Behavior:
 * - Values include CURRENT, ARREAR, ADVANCE, penalty/rebate types, and OTHERS.
 * - Serialized to JSON as the enum string value via {@link #toString()} and {@link JsonValue}.
 * - Deserialized from API text with {@link #fromValue(String)}.
 *
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

	Purpose(String value) {
		this.value = value;
	}

	
	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static Purpose fromValue(String text) {
		for (Purpose b : Purpose.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
}