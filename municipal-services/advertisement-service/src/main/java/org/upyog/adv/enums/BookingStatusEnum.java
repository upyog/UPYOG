/**
 * Enumeration of all possible states for an advertisement booking.
 * 
 * This enum represents the complete lifecycle of an advertisement booking:
 * - AVAILABLE: Slot is open for booking
 * - BOOKING_CREATED: Initial booking request made
 * - BOOKED: Booking confirmed after successful payment
 * - CANCELLATION_REQUESTED: User has requested to cancel booking
 * - PENDING_FOR_PAYMENT: Awaiting payment confirmation
 * - PAYMENT_FAILED: Payment attempt unsuccessful
 * - CANCELLED: Booking has been cancelled
 * - BOOKING_EXPIRED: Booking expired due to payment timeout
 * 
 * The enum includes a status field and getter method to retrieve
 * the status as a string value when needed.
 */
package org.upyog.adv.enums;

public enum BookingStatusEnum {
	AVAILABLE,
	BOOKING_CREATED,
	BOOKED,
	CANCELLATION_REQUESTED,
	PENDING_FOR_PAYMENT,
	PAYMENT_FAILED,
	CANCELLED,
	BOOKING_EXPIRED;
	
	String status;
	
	public String getStatus() {
		return status;
	}

}
