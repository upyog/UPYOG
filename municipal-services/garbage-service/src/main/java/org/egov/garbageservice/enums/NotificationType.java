package org.egov.garbageservice.enums;

/**
 * Channel types requested when fetching notification templates or sending alerts.
 *
 * Behavior:
 * - Values: SYSTEM, MAIL, SMS — select which notification channels to include.
 * - Used as a list on {@link org.egov.garbageservice.model.contract.NotificationDetailsRequest}.
 *
 * Notes:
 * - Does not perform sending itself; downstream notification service interprets the types.
 * - Pair with {@link SMSCategory} when building actual SMS payloads.
 */
public enum NotificationType {
	SYSTEM, MAIL, SMS;

}
