package org.upyog.adv.service;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.util.PaymentTimerKeyBuilder;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.upyog.adv.web.models.BookingPaymentTimerDetails;

import lombok.extern.slf4j.Slf4j;

/**
 * Payment timer holds are stored in {@code eg_adv_payment_timer} ({@link BookingPaymentTimerDetails}).
 * Redis (optional) mirrors the same keys when {@code adv.payment.timer.redis.enabled=true}.
 */
@Service
@Slf4j
public class PaymentTimerService {

	private final BookingRepository bookingRepository;
	private final ObjectProvider<PaymentTimerRedisService> paymentTimerRedis;

	public PaymentTimerService(@Lazy BookingRepository bookingRepository,
			ObjectProvider<PaymentTimerRedisService> paymentTimerRedis) {
		this.bookingRepository = bookingRepository;
		this.paymentTimerRedis = paymentTimerRedis;
	}

	/**
	 * Creates payment timer holds for the requested advertisement slots.
	 *
	 * <p>
	 * When the booking has not been created yet, the timer table stores a generated
	 * draft id in {@code booking_id}. The final booking create flow later replaces
	 * that draft id with the actual booking id and booking number.
	 * </p>
	 *
	 * @param criteriaList                 slot search criteria used to build timer rows
	 * @param requestInfo                  request metadata and authenticated user details
	 */
	@Transactional
	public long insertBookingIdForTimer(List<AdvertisementSlotSearchCriteria> criteriaList, RequestInfo requestInfo) {
		var uuid = requestInfo.getUserInfo().getUuid();
		var tenantId = criteriaList.stream().map(AdvertisementSlotSearchCriteria::getTenantId)
				.filter(StringUtils::isNotBlank).findFirst()
				.orElse(requestInfo.getUserInfo().getTenantId());
		var existingDraftId = bookingRepository.fetchDraftIdForTimer(criteriaList, uuid, tenantId);
		var willInsertNewTimer = existingDraftId == null;

		List<BookingPaymentTimerDetails> timerDetails = Collections.emptyList();
		String preGeneratedDraftId = null;
		var redis = paymentTimerRedis.getIfAvailable();

		if (willInsertNewTimer) {
			preGeneratedDraftId = BookingUtil.getRandonUUID();
			var createdTime = BookingUtil.getCurrentTimestamp();
			timerDetails = PaymentTimerKeyBuilder.buildTimerDetailsList(criteriaList, preGeneratedDraftId, uuid,
					tenantId, createdTime);
			acquireRedisSlotHolds(redis, timerDetails);
		}

		try {
			long timerValue = bookingRepository.insertBookingIdForTimer(criteriaList, requestInfo, preGeneratedDraftId);
			if (redis != null && !CollectionUtils.isEmpty(timerDetails)) {
				redis.syncTimerRows(timerDetails);
			}
			return timerValue;
		} catch (RuntimeException ex) {
			if (redis != null && !CollectionUtils.isEmpty(timerDetails)) {
				redis.removeTimerRows(timerDetails);
			}
			throw ex;
		}
	}

	private void acquireRedisSlotHolds(PaymentTimerRedisService redis, List<BookingPaymentTimerDetails> timerDetails) {
		if (redis == null) {
			return;
		}
		for (var detail : timerDetails) {
			if (!redis.tryAcquireSlot(detail)) {
				throw new CustomException("SLOT_PAYMENT_TIMER_LOCKED",
						"Advertisement slot is held by another booking. addType=" + detail.getAddType()
								+ " location=" + detail.getLocation() + " bookingDate=" + detail.getBookingDate());
			}
		}
	}

	/**
	 * Deletes timer rows for an advertisement booking id and removes the optional
	 * Redis mirror first.
	 *
	 * @param bookingId   booking id or draft id used in the timer table
	 * @param requestInfo request metadata
	 */
	@Transactional
	public void deleteBookingIdForTimer(String bookingId, RequestInfo requestInfo) {
		log.info("Deleting timer entry for booking id : {}", bookingId);
		removeRedisMirrorForBooking(bookingId);
		bookingRepository.deleteBookingIdForTimer(bookingId);
	}

	/**
	 * Deletes timer and draft data for a user when no specific draft id or booking id
	 * is supplied.
	 *
	 * @param uuid      authenticated user uuid
	 * @param draftId   draft id to retain when present
	 * @param bookingId booking id to retain when present
	 */
	@Transactional
	public void deleteDataFromTimerAndDraft(String uuid, String draftId, String bookingId) {
		if (StringUtils.isBlank(draftId) && StringUtils.isBlank(bookingId)) {
			removeRedisMirrorForUser(uuid);
		}
		bookingRepository.deleteDataFromTimerAndDraft(uuid, draftId, bookingId);
	}

	/**
	 * Removes Redis mirror entries for timer rows still identified by draft id.
	 *
	 * @param draftId draft id stored in the timer table as booking id
	 */
	@Transactional
	public void removeRedisMirrorForDraft(String draftId) {
		removeRedisMirrorForBooking(draftId);
	}

	/**
	 * Removes Redis mirror rows for a single timer booking reference.
	 *
	 * @param bookingId booking id or draft id used by timer rows
	 */
	private void removeRedisMirrorForBooking(String bookingId) {
		var redis = paymentTimerRedis.getIfAvailable();
		if (redis == null || StringUtils.isBlank(bookingId)) {
			return;
		}
		var timers = filterRedisEligibleTimers(bookingRepository.getPaymentTimerByBookingId(bookingId));
		if (!CollectionUtils.isEmpty(timers)) {
			redis.removeTimerRows(timers);
		}
	}

	/**
	 * Removes Redis mirror rows for all timer holds created by a user.
	 *
	 * @param uuid authenticated user uuid
	 */
	private void removeRedisMirrorForUser(String uuid) {
		var redis = paymentTimerRedis.getIfAvailable();
		if (redis == null || StringUtils.isBlank(uuid)) {
			return;
		}
		var timers = filterRedisEligibleTimers(bookingRepository.getPaymentTimerByCreatedBy(uuid));
		if (!CollectionUtils.isEmpty(timers)) {
			redis.removeTimerRows(timers);
		}
	}

	/**
	 * Filters out partial timer rows that cannot be represented safely as Redis slot
	 * keys.
	 *
	 * @param timers timer rows from the database
	 * @return timer rows with all Redis key dimensions present
	 */
	private List<BookingPaymentTimerDetails> filterRedisEligibleTimers(List<BookingPaymentTimerDetails> timers) {
		return timers.stream()
				.filter(timer -> StringUtils.isNotBlank(timer.getTenantId()) && timer.getBookingDate() != null
						&& StringUtils.isNotBlank(timer.getAddType()) && StringUtils.isNotBlank(timer.getLocation())
						&& StringUtils.isNotBlank(timer.getFaceArea()))
				.toList();
	}
}
