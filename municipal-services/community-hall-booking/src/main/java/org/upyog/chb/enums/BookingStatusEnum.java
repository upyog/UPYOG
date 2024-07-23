package org.upyog.chb.enums;

public enum BookingStatusEnum {
	
	BOOKING_CREATED,
	BOOKED,
	CANCELLATION_REQUESTED,
	CANCELLED;
	
	String status;
	
	public String getStatus() {
		return status;
	}

}
