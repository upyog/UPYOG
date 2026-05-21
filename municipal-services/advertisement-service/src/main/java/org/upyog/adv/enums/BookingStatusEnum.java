package org.upyog.adv.enums;

public enum BookingStatusEnum {
	AVAILABLE,
	BOOKING_CREATED,
	BOOKED,
	CANCELLATION_REQUESTED,
	PENDING_FOR_PAYMENT,
	PENDING_FOR_VERIFICATION,
	PAYMENT_FAILED,
	CANCELLED,
	BOOKING_EXPIRED,
    PENDING_FOR_INSPECTION;
	String status;
	
	public String getStatus() {
		return status;
	}

}
