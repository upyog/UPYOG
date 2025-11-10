package org.upyog.chb.enums;


public enum BookingStatusEnum {
	AVAILABLE,                  // System-specific (not in workflow, but useful)
	BOOKING_CREATED,            // Initial booking creation (before workflow starts)

	INITIATED,                  // After INITIATE action
	PENDING_FOR_VERIFICATION,       // "PENDING_FOR_VERIFICATION" in workflow
	CITIZEN_ACTION_REQUIRED,    // "CITIZEN_ACTION_REQUIRED" in workflow
	PENDING_PAYMENT,            // "PENDING_PAYMENT" in workflow
	PAYMENT_DONE,               // "PAYMENT_DONE" in workflow
	VENUE_INSPECTION,           // "VENUE_INSPECTION" in workflow
	REFUND_COMPLETED,           // "REFUND_COMPLETED" (terminate state)

	BOOKED,                     // Optional if you need a business flag after payment
	CANCELLATION_REQUESTED,     // If cancellation flow is explicitly triggered
	PAYMENT_FAILED,             // If payment gateway fails
	CANCELLED,                  // Hard-cancelled before/without refund
	EXPIRED;                    // System expiry

	private String status;

	public String getStatus() {
		return this.name();
	}
}

