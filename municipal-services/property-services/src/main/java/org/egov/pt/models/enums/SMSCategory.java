package org.egov.pt.models.enums;

public enum SMSCategory {
	OTP, TRANSACTION, PROMOTION, NOTIFICATION, OTHERS;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
