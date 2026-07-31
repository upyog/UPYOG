package org.egov.garbageservice.enums;

/**
 * Category tag for outbound SMS sent through the eGov SMS gateway.
 * <p>
 * Behavior:
 * - Values: OTP, TRANSACTION, PROMOTION, NOTIFICATION, OTHERS.
 * - {@link #toString()} returns lowercase enum name for gateway payloads.
 * <p>
 * Notes:
 * - {@link org.egov.garbageservice.service.NotificationService} uses NOTIFICATION for bill reminders.
 * - Category may affect routing, throttling, or template rules on the SMS service — follow gateway docs.
 */
public enum SMSCategory {
    OTP, TRANSACTION, PROMOTION, NOTIFICATION, OTHERS;

    /**
     * Executes toString utility operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates input parameters.</li>
     *   <li>Executes helper business logic or transformation.</li>
     *   <li>Returns formatted output result.</li>
     * </ol>
     *
     * @return the output result
     */

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
