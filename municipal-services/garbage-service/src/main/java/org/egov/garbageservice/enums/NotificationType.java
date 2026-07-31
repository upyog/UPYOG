package org.egov.garbageservice.enums;

/**
 * Channel types requested when fetching notification templates or sending alerts.
 * <p>
 * Behavior:
 * - Values: SYSTEM, MAIL, SMS — select which notification channels to include.
 * <p>
 * Notes:
 * - Does not perform sending itself; downstream notification service interprets the types.
 * - Pair with {@link SMSCategory} when building actual SMS payloads.
 */
public enum NotificationType {
    SYSTEM, MAIL, SMS;

}
