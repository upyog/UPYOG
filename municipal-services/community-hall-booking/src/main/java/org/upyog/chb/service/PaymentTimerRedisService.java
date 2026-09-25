package org.upyog.chb.service;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.util.PaymentTimerKeyBuilder;
import org.upyog.chb.web.models.BookingPaymentTimerDetails;

import lombok.extern.slf4j.Slf4j;

/**
 * Optional Redis mirror for payment timer rows and slot holds.
 * Enabled when {@code chb.payment.timer.redis.enabled=true}; complements {@code eg_chb_payment_timer}.
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "chb.payment.timer.redis.enabled", havingValue = "true")
public class PaymentTimerRedisService {

	private final StringRedisTemplate redis;
	private final CommunityHallBookingConfiguration bookingConfiguration;

	public PaymentTimerRedisService(StringRedisTemplate redis,
			CommunityHallBookingConfiguration bookingConfiguration) {
		this.redis = redis;
		this.bookingConfiguration = bookingConfiguration;
	}

	/**
	 * Attempts to acquire a Redis slot hold for the given timer detail and time window.
	 *
	 * @param detail timer attributes used to build the slot key
	 * @param startTime slot start time segment
	 * @param endTime slot end time segment
	 * @return {@code true} when the slot is held by the caller (new or renewed), {@code false} when another user holds it
	 */
	public boolean tryAcquireSlot(BookingPaymentTimerDetails detail,String startTime , String endTime) {
		var key = PaymentTimerKeyBuilder.toRedisSlotKey(detail,startTime , endTime);
		var value = PaymentTimerKeyBuilder.toRedisValue(detail.getCreatedBy(), detail.getBookingId());
		var ttl = paymentTimerDuration();
		var acquired = Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(key, value, ttl));
		if (acquired) {
			return true;
		}
		var existing = redis.opsForValue().get(key);
		if (value.equals(existing)) {
			redis.expire(key, ttl);
			return true;
		}
		log.info("Redis slot hold conflict key={} existingHolder={}", key, existing);
		return false;
	}

	/**
	 * Writes timer-row keys to Redis with the configured payment-timer TTL.
	 *
	 * @param details timer rows persisted in the database
	 * @param fromTime slot start time segment used in Redis keys
	 * @param toTime slot end time segment used in Redis keys
	 */
	public void syncTimerRows(List<BookingPaymentTimerDetails> details, String fromTime, String toTime) {
		var ttl = paymentTimerDuration();
		for (var detail : details) {
			var rowKey = PaymentTimerKeyBuilder.toRedisTimerRowKey(detail,fromTime,toTime);
			var value = PaymentTimerKeyBuilder.toRedisValue(detail.getCreatedBy(), detail.getBookingId());
			redis.opsForValue().set(rowKey, value, ttl);
			log.debug("Redis timer row key synced: {}", rowKey);
		}
	}

	/**
	 * Removes timer-row and slot-hold keys from Redis for the supplied timer details.
	 *
	 * @param details timer rows whose Redis mirrors should be deleted
	 * @param fromTime slot start time segment used in Redis keys
	 * @param toTime slot end time segment used in Redis keys
	 */
	public void removeTimerRows(List<BookingPaymentTimerDetails> details, String fromTime, String toTime) {
		for (var detail : details) {
			redis.delete(PaymentTimerKeyBuilder.toRedisTimerRowKey(detail,fromTime , toTime));
			
			if (fromTime == null && detail != null && detail.getStartTime() != null)
				fromTime = detail.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
			
			if (toTime == null && detail != null && detail.getEndTime() != null)
				toTime = detail.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
			
			redis.delete(PaymentTimerKeyBuilder.toRedisSlotKey(detail,fromTime ,toTime));
		}
	}

	private Duration paymentTimerDuration() {
		return Duration.ofMinutes(Integer.parseInt(bookingConfiguration.getBookingPaymentTimerValue()));
	}
}
