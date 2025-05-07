package org.egov.pg.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TxnSettlementStatus {
	CREATED("CREATED"), SETTLED("SETTLED"), FAILED("FAILED");

	@JsonCreator
	public static TxnSettlementStatus fromValue(String text) {
		for (TxnSettlementStatus b : TxnSettlementStatus.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}

	private String value;

	TxnSettlementStatus(String value) {
		this.value = value;
	}

}
