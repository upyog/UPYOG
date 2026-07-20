package org.upyog.chb.util;

import java.time.LocalDate;

import org.apache.commons.lang.StringUtils;
import org.upyog.chb.web.models.BookingPaymentTimerDetails;

import lombok.extern.slf4j.Slf4j;

/**
 * Keys aligned with {@code eg_chb_payment_timer} columns (tenant, community hall, hall, date, booking id).
 */
@Slf4j
public final class PaymentTimerKeyBuilder {

	private static final String REDIS_TIMER_PREFIX = "chb:payment-timer:row:";
	private static final String REDIS_SLOT_PREFIX = "chb:payment-timer:slot:";

	private PaymentTimerKeyBuilder() {
	}

	/**
	 * Builds a Redis timer-row key from {@link BookingPaymentTimerDetails}.
	 * One key per timer row (matches DB uniqueness: hall + booking code + date+time).
	 *
	 * @param detail timer row attributes (tenant, venue, unit, booking date/id)
	 * @param slotStartTime slot start time segment used in the key
	 * @param slotEndTime slot end time segment used in the key
	 * @return Redis key for the payment timer row
	 */
	public static String toRedisTimerRowKey(BookingPaymentTimerDetails detail, String slotStartTime, String slotEndTime) {
		validate(detail.getTenantId(), detail.getVenueCode(), detail.getUnitCode(), detail.getBookingDate(),
				detail.getBookingId());
		return REDIS_TIMER_PREFIX + String.join(":", detail.getTenantId(), detail.getVenueCode(), detail.getUnitCode(),
				detail.getBookingDate().toString(), detail.getBookingId(), slotStartTime, slotEndTime);
	}

	/**
	 * Builds a Redis slot-hold key from {@link BookingPaymentTimerDetails}.
	 * Slot holds prevent double booking on the same physical slot (tenant + hall + date + time).
	 *
	 * @param detail timer row attributes (tenant, venue, unit, booking date)
	 * @param startTime slot start time segment used in the key
	 * @param endTime slot end time segment used in the key
	 * @return Redis key used to hold a slot during payment timer
	 */
	public static String toRedisSlotKey(BookingPaymentTimerDetails detail, String startTime, String endTime) {
		return toRedisSlotKey(detail.getTenantId(), detail.getVenueCode(), detail.getUnitCode(), detail.getBookingDate(),
				startTime, endTime);
	}

	/**
	 * Builds a Redis slot-hold key from explicit slot attributes.
	 *
	 * @param tenantId tenant identifier
	 * @param venueCode parent venue/community hall code
	 * @param unitCode booked unit/hall code
	 * @param bookingDate booking date
	 * @param startTime slot start time segment used in the key
	 * @param endTime slot end time segment used in the key
	 * @return Redis key used to hold a slot during payment timer
	 * @throws IllegalArgumentException when required key parts are blank or null
	 */
	public static String toRedisSlotKey(String tenantId, String venueCode, String unitCode, LocalDate bookingDate,
			String startTime, String endTime) {
		log.info("validate : tenantId - {}, venueCode - {}, unitCode - {}, bookingDate - {}, startTime - {} , endTime - {} ",
				safe(tenantId), safe(venueCode), safe(unitCode), safe(bookingDate),safe(startTime),safe(endTime));
		if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(venueCode) || StringUtils.isBlank(unitCode)
				|| bookingDate == null || startTime == null || endTime == null) {
			throw new IllegalArgumentException("tenantId, venueCode, unitCode and bookingDate , startTime , endTime are required");
		}
		return REDIS_SLOT_PREFIX + String.join(":", tenantId, venueCode, unitCode, bookingDate.toString(),
				startTime, endTime);
	}

	/**
	 * Serializes the Redis value stored for a timer or slot hold.
	 *
	 * @param createdBy UUID of the user who created the timer hold
	 * @param bookingId booking or draft reference id
	 * @return value in the form {@code createdBy|bookingId}
	 */
	public static String toRedisValue(String createdBy, String bookingId) {
		return createdBy + "|" + bookingId;
	}

	/**
	 * Creates a {@link BookingPaymentTimerDetails} instance for timer persistence and Redis mirroring.
	 *
	 * @param tenantId tenant identifier
	 * @param venueCode parent venue/community hall code
	 * @param unitCode booked unit/hall code
	 * @param bookingDate booking date
	 * @param bookingId booking or draft reference id
	 * @param userId UUID of the user creating the timer
	 * @param createdTime creation timestamp in epoch milliseconds
	 * @return populated timer details with {@code ACTIVE} status
	 */
	public static BookingPaymentTimerDetails toTimerDetails(String tenantId, String venueCode, String unitCode,
			LocalDate bookingDate, String bookingId, String userId, long createdTime) {
		var details = new BookingPaymentTimerDetails();
		details.setTenantId(tenantId);
		details.setVenueCode(venueCode);
		details.setUnitCode(unitCode);
		details.setBookingDate(bookingDate);
		details.setBookingId(bookingId);
		details.setCreatedBy(userId);
		details.setCreatedTime(createdTime);
		details.setStatus("ACTIVE");
		return details;
	}

	private static void validate(String tenantId, String venueCode, String unitCode, LocalDate bookingDate,
			String bookingId) {
		log.info("validate : tenantId - {}, venueCode - {}, unitCode - {}, bookingDate - {}, bookingId - {}",
				safe(tenantId), safe(venueCode), safe(unitCode), safe(bookingDate), safe(bookingId));
		if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(venueCode) || StringUtils.isBlank(unitCode)
				|| bookingDate == null || StringUtils.isBlank(bookingId)) {
			throw new IllegalArgumentException(
					"tenantId, venueCode, unitCode, bookingDate and bookingId are required");
		}
	}
	
	private static String safe(Object value) {
		return value == null ? "N/A" : value.toString();
	}
	
}
