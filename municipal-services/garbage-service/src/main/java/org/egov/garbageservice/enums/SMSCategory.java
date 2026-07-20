package org.egov.garbageservice.enums;

/**
 * Category tag for outbound SMS sent through the eGov SMS gateway.
 *
 * Behavior:
 * - Values: OTP, TRANSACTION, PROMOTION, NOTIFICATION, OTHERS.
 * - {@link #toString()} returns lowercase enum name for gateway payloads.
 * - Set on {@link org.egov.garbageservice.model.SMSSentRequest} when building SMS requests.
 *
 * Notes:
 * - {@link org.egov.garbageservice.service.NotificationService} uses NOTIFICATION for bill reminders.
 * - Category may affect routing, throttling, or template rules on the SMS service — follow gateway docs.
 */
public enum SMSCategory {
	OTP, TRANSACTION, PROMOTION, NOTIFICATION, OTHERS;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
