package org.upyog.adv.util;

import java.time.LocalDate;
import java.util.ArrayList;

import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.upyog.adv.web.models.BookingPaymentTimerDetails;

/**
 * Keys aligned with {@code eg_adv_payment_timer} columns (tenant, slot dimensions, date, booking id).
 */
public final class PaymentTimerKeyBuilder {

	private static final String REDIS_TIMER_PREFIX = "adv:payment-timer:row:";
	private static final String REDIS_SLOT_PREFIX = "adv:payment-timer:slot:";

	private PaymentTimerKeyBuilder() {
	}

	public static String toRedisTimerRowKey(BookingPaymentTimerDetails detail) {
		return toRedisTimerRowKey(detail.getTenantId(), detail.getAddType(), detail.getLocation(),
				detail.getFaceArea(), detail.getNightLight(), detail.getBookingDate(), detail.getBookingId());
	}

	public static String toRedisTimerRowKey(String tenantId, String addType, String location, String faceArea,
			Boolean nightLight, LocalDate bookingDate, String bookingId) {
		validateSlotDimensions(tenantId, addType, location, faceArea, bookingDate);
		if (org.apache.commons.lang.StringUtils.isBlank(bookingId)) {
			throw new IllegalArgumentException("bookingId is required");
		}
		return REDIS_TIMER_PREFIX + String.join(":", tenantId, addType, location, faceArea, nightLightValue(nightLight),
				bookingDate.toString(), bookingId);
	}

	public static String toRedisSlotKey(BookingPaymentTimerDetails detail) {
		return toRedisSlotKey(detail.getTenantId(), detail.getAddType(), detail.getLocation(), detail.getFaceArea(),
				detail.getNightLight(), detail.getBookingDate());
	}

	public static String toRedisSlotKey(String tenantId, String addType, String location, String faceArea,
			Boolean nightLight, LocalDate bookingDate) {
		validateSlotDimensions(tenantId, addType, location, faceArea, bookingDate);
		return REDIS_SLOT_PREFIX + String.join(":", tenantId, addType, location, faceArea, nightLightValue(nightLight),
				bookingDate.toString());
	}

	public static String toRedisValue(String createdBy, String bookingId) {
		return createdBy + "|" + bookingId;
	}

	public static java.util.List<BookingPaymentTimerDetails> buildTimerDetailsList(
			java.util.List<AdvertisementSlotSearchCriteria> criteriaList, String draftId, String userId, String tenantId,
			long createdTime) {
		var timerDetails = new ArrayList<BookingPaymentTimerDetails>();
		for (var criteria : criteriaList) {
			var startDate = LocalDate.parse(criteria.getBookingStartDate());
			var endDate = LocalDate.parse(criteria.getBookingEndDate());
			while (!startDate.isAfter(endDate)) {
				timerDetails.add(toTimerDetails(criteria, tenantId, startDate, draftId, userId, createdTime));
				startDate = startDate.plusDays(1);
			}
		}
		return timerDetails;
	}

	public static BookingPaymentTimerDetails toTimerDetails(AdvertisementSlotSearchCriteria criteria, String tenantId,
			LocalDate bookingDate, String bookingId, String userId, long createdTime) {
		return BookingPaymentTimerDetails.builder().tenantId(tenantId).addType(criteria.getAddType())
				.location(criteria.getLocation()).faceArea(criteria.getFaceArea()).nightLight(criteria.getNightLight())
				.bookingDate(bookingDate).bookingId(bookingId).createdBy(userId).createdTime(createdTime)
				.status("ACTIVE").build();
	}

	private static void validateSlotDimensions(String tenantId, String addType, String location, String faceArea,
			LocalDate bookingDate) {
		if (org.apache.commons.lang.StringUtils.isBlank(tenantId) || org.apache.commons.lang.StringUtils.isBlank(addType)
				|| org.apache.commons.lang.StringUtils.isBlank(location)
				|| org.apache.commons.lang.StringUtils.isBlank(faceArea) || bookingDate == null) {
			throw new IllegalArgumentException("tenantId, addType, location, faceArea and bookingDate are required");
		}
	}

	private static String nightLightValue(Boolean nightLight) {
		return Boolean.TRUE.equals(nightLight) ? "true" : "false";
	}
}
