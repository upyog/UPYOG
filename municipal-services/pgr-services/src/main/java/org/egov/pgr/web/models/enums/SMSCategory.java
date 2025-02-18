package org.egov.pgr.web.models.enums;

public enum SMSCategory {
	OTP, TRANSACTION, PROMOTION, NOTIFICATION, OTHERS;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
