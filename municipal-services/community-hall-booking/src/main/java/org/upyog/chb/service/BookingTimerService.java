package org.upyog.chb.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.util.CommunityHallSlotCriteriaUtil;
import org.upyog.chb.util.PaymentTimerKeyBuilder;
import org.upyog.chb.web.models.BookingPaymentTimerDetails;
import org.upyog.chb.web.models.VenueSlotAvailabilityDetail;
import org.upyog.chb.web.models.VenueSlotSearchCriteria;

import lombok.extern.slf4j.Slf4j;

/**
 * Payment timer holds are stored in {@code eg_chb_payment_timer} ({@link BookingPaymentTimerDetails}).
 * Redis (optional) mirrors the same keys — no {@code seat_locks} table.
 */
@Service
@Slf4j
public class BookingTimerService {

	private final CommunityHallBookingRepository bookingRepository;
	private final CommunityHallBookingConfiguration bookingConfiguration;
	private final ObjectProvider<PaymentTimerRedisService> paymentTimerRedis;
	private final BookingTimerService self;

	public BookingTimerService(CommunityHallBookingRepository bookingRepository,
			CommunityHallBookingConfiguration bookingConfiguration,
			ObjectProvider<PaymentTimerRedisService> paymentTimerRedis,
			@Lazy BookingTimerService self) {
		this.bookingRepository = bookingRepository;
		this.bookingConfiguration = bookingConfiguration;
		this.paymentTimerRedis = paymentTimerRedis;
		this.self = self;
	}

	@Transactional
	public long managePaymentTimer(VenueSlotSearchCriteria criteria, RequestInfo info,
			List<VenueSlotAvailabilityDetail> availabilityDetailsList) {
		validateTimerCriteria(criteria, info);
		ensureTimerBookingReference(criteria);

		var existingTimers = bookingRepository.getBookingTimer(criteria);
		if (!CollectionUtils.isEmpty(existingTimers)) {
			log.info("Reusing existing payment timer for bookingId={}", getTimerBookingReference(criteria));
			return getTimerValue(existingTimers.get(0));
		}

		var userId = info.getUserInfo().getUuid();
		var hallCodes = CommunityHallSlotCriteriaUtil.resolveHallCodes(criteria);
		var bookingDates = CommunityHallSlotCriteriaUtil.resolveBookingDates(criteria);
		var createdTime = CommunityHallBookingUtil.getCurrentTimestamp();
		var activeTimersInRange = bookingRepository.getBookingTimerByCreatedBy(info, criteria);
		String fromTime = criteria.getFromTime();
		String toTime = criteria.getToTime();
		var timerDetails = new ArrayList<BookingPaymentTimerDetails>();
		var redis = paymentTimerRedis.getIfAvailable();

		for (var bookingDate : bookingDates) {
			for (var hallCode : hallCodes) {
				assertNoConflictingTimer(activeTimersInRange, criteria, userId, hallCode, bookingDate);

				var detail = PaymentTimerKeyBuilder.toTimerDetails(criteria.getTenantId(),
						criteria.getVenueCode(), hallCode, bookingDate, getTimerBookingReference(criteria), userId,
						createdTime);

				if (redis != null && !redis.tryAcquireSlot(detail,fromTime, toTime)) {
					throw new CustomException("SLOT_PAYMENT_TIMER_LOCKED",
							"Hall slot is held by another booking. hallCode=" + hallCode + " bookingDate=" + bookingDate);
				}
				timerDetails.add(detail);
			}
		}

		try {
			boolean isFinalBooking = criteria.getBookingId() != null && !criteria.getBookingId().isEmpty();
			bookingRepository.createBookingTimer(criteria, info, isFinalBooking, timerDetails);
			if (redis != null) {
				redis.syncTimerRows(timerDetails,fromTime , toTime);
			}
		} catch (RuntimeException ex) {
			if (redis != null) {
				redis.removeTimerRows(timerDetails,fromTime , toTime);
			}
			throw ex;
		}

		var timerValue = CommunityHallBookingUtil
				.getSeconds(Integer.parseInt(bookingConfiguration.getBookingPaymentTimerValue()));
		log.info("Payment timer created bookingId={} hallCodes={} timerValueSeconds={}", getTimerBookingReference(criteria),
				hallCodes, timerValue);
		return timerValue;
	}

	@Transactional
	public long getTimerValue(VenueSlotSearchCriteria criteria, RequestInfo info,
			List<VenueSlotAvailabilityDetail> availabilityDetailsList) {
		return self.managePaymentTimer(criteria, info, availabilityDetailsList);
	}

	public long getRemainingTimerValue(String bookingId) {
		if (StringUtils.isBlank(bookingId)) {
			return 0L;
		}
		var criteria = VenueSlotSearchCriteria.builder().bookingId(bookingId).build();
		var timers = bookingRepository.getBookingTimer(criteria);
		if (CollectionUtils.isEmpty(timers)) {
			return 0L;
		}
		return Math.max(getTimerValue(timers.get(0)), 0L);
	}

	private void assertNoConflictingTimer(List<BookingPaymentTimerDetails> activeTimersInRange,
			VenueSlotSearchCriteria criteria, String userId, String hallCode,
			java.time.LocalDate bookingDate) {
		var conflict = activeTimersInRange.stream()
				.anyMatch(t -> hallCode.equals(t.getUnitCode()) && bookingDate.equals(t.getBookingDate())
						&& !(userId.equals(t.getCreatedBy()) && getTimerBookingReference(criteria).equals(t.getBookingId())));
		if (conflict) {
			throw new CustomException("SLOT_PAYMENT_TIMER_LOCKED",
					"Hall slot already has an active payment timer. hallCode=" + hallCode + " bookingDate=" + bookingDate);
		}
	}

	private void validateTimerCriteria(VenueSlotSearchCriteria criteria, RequestInfo info) {
		if (info == null || info.getUserInfo() == null || StringUtils.isBlank(info.getUserInfo().getUuid())) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_SEARCH,
					"Authenticated user is required for payment timer");
		}
		CommunityHallSlotCriteriaUtil.resolveHallCodes(criteria);
	}

	private void ensureTimerBookingReference(VenueSlotSearchCriteria criteria) {
		if (StringUtils.isBlank(criteria.getBookingId()) && StringUtils.isBlank(criteria.getDraftId())) {
			criteria.setDraftId(CommunityHallBookingUtil.getRandonUUID());
		}
	}

	private String getTimerBookingReference(VenueSlotSearchCriteria criteria) {
		return StringUtils.isNotBlank(criteria.getBookingId()) ? criteria.getBookingId() : criteria.getDraftId();
	}

	private Long getTimerValue(BookingPaymentTimerDetails bookingPaymentTimerDetails) {
		long timerValue = CommunityHallBookingUtil
				.getSeconds(Integer.parseInt(bookingConfiguration.getBookingPaymentTimerValue()));
		long currentTimestamp = CommunityHallBookingUtil.getCurrentTimestamp();
		long timeSpentAfterCreation = CommunityHallBookingUtil.calculateDifferenceInSeconds(currentTimestamp,
				bookingPaymentTimerDetails.getCreatedTime());
		log.info("currentTimestamp : {} createdTime : {} timeSpentAfterCreation : {} ", currentTimestamp,
				bookingPaymentTimerDetails.getCreatedTime(), timeSpentAfterCreation);
		return timerValue - timeSpentAfterCreation;
	}

	@Transactional
	public void deleteBookingTimer(String bookingId, boolean updateBookingStatus) {
		log.info("Deleting timer entry for booking id : {}", bookingId);
		removeRedisMirrorForBooking(bookingId);
		bookingRepository.deleteBookingTimer(bookingId, updateBookingStatus);
	}

	private void removeRedisMirrorForBooking(String bookingId) {
		var redis = paymentTimerRedis.getIfAvailable();
		if (redis == null) {
			return;
		}
		var criteria = VenueSlotSearchCriteria.builder().bookingId(bookingId).build();
		var timers = bookingRepository.getBookingTimer(criteria);
		if (!CollectionUtils.isEmpty(timers)) {
			redis.removeTimerRows(timers,criteria.getFromTime(),criteria.getToTime());
		}
	}

	public List<BookingPaymentTimerDetails> getBookingFromTimerTable(RequestInfo info,
			VenueSlotSearchCriteria criteria) {
		return bookingRepository.getBookingTimerByCreatedBy(info, criteria);
	}
}
