package org.upyog.chb.enums;

public enum BookingStatusEnum {
	
	BOOKING_CREATED,
	BOOKED,
	CANCELLATION_REQUESTED,
	PENDING_FOR_PAYMENT,
	PAYMENT_FAILED,
	CANCELLED;
	
	String status;
	
	public String getStatus() {
		return status;
	}

}
