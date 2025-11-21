package org.upyog.chb.enums;

/**
 * This enum defines the various booking statuses used in the Community Hall Booking module.
 * 
 * Purpose:
 * - To represent the different states a booking can have during its lifecycle.
 * - To ensure consistency and avoid hardcoding booking status values across the application.
 * 
 * Enum Values:
 * 1. AVAILABLE:
 *    - Indicates that the community hall is available for booking.
 * 
 * 2. BOOKING_CREATED:
 *    - Indicates that a booking has been created but not yet confirmed.
 * 
 * 3. BOOKED:
 *    - Indicates that the booking has been successfully confirmed.
 * 
 * 4. CANCELLATION_REQUESTED:
 *    - Indicates that a cancellation request has been initiated for the booking.
 * 
 * 5. PENDING_FOR_PAYMENT:
 *    - Indicates that the booking is awaiting payment.
 * 
 * 6. PAYMENT_FAILED:
 *    - Indicates that the payment for the booking has failed.
 * 
 * 7. CANCELLED:
 *    - Indicates that the booking has been cancelled.
 * 
 * 8. EXPIRED:
 *    - Indicates that the booking has expired due to non-payment or other reasons.
 * 
 * 9. REFUNDPENDING:
 *    - Indicates that a refund is pending for the booking.
 * 
 * 10. INSPECTIONPENDING:
 *     - Indicates that an inspection is pending for the booking.
 * 
 * 11. REFUNDAPPROVALPENDING:
 *     - Indicates that the refund approval process is pending.
 * 
 * 12. REFUNDAPPROVED:
 *     - Indicates that the refund has been approved.
 * 
 * 13. REJECTED:
 *     - Indicates that the booking or refund request has been rejected.
 * 
 * Usage:
 * - This enum is used throughout the application to handle and validate booking statuses.
 */
public enum BookingStatusEnum {
	AVAILABLE,
	BOOKING_CREATED,
	BOOKED,
	CANCELLATION_REQUESTED,
	PENDING_FOR_PAYMENT,
	PAYMENT_FAILED,
	CANCELLED,
	EXPIRED,
	REFUNDPENDING,
	INSPECTIONPENDING,
	REFUNDAPPROVALPENDING,
	REFUNDAPPROVED,
	REJECTED;
	
	
	String status;
	
	public String getStatus() {
		return status;
	}

}
