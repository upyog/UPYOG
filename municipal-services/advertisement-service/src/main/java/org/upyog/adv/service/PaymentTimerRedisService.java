package org.upyog.adv.service;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.util.PaymentTimerKeyBuilder;
import org.upyog.adv.web.models.BookingPaymentTimerDetails;

import lombok.extern.slf4j.Slf4j;

/**
 * Optional Redis mirror of {@code eg_adv_payment_timer} rows — keys use the same slot + date dimensions.
 * Not used when {@code adv.payment.timer.redis.enabled=false} (DB is the source of truth).
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "adv.payment.timer.redis.enabled", havingValue = "true")
public class PaymentTimerRedisService {

	private final StringRedisTemplate redis;
	private final BookingConfiguration bookingConfiguration;

	public PaymentTimerRedisService(StringRedisTemplate redis, BookingConfiguration bookingConfiguration) {
		this.redis = redis;
		this.bookingConfiguration = bookingConfiguration;
	}

	/**
	 * Attempts to acquire a Redis-based slot hold for the given timer detail.
	 *
	 * <p>If the slot is already held by the current booking, the TTL is refreshed.
	 * Otherwise, the method returns {@code false} when another booking holds the slot.</p>
	 *
	 * @param detail timer details used to build the Redis key
	 * @return {@code true} when the slot hold is acquired or refreshed; otherwise {@code false}
	 */
	public boolean tryAcquireSlot(BookingPaymentTimerDetails detail) {
		var key = PaymentTimerKeyBuilder.toRedisSlotKey(detail);
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
	 * Synchronizes timer row entries into Redis for the provided timer details.
	 *
	 * <p>This method stores a Redis key for each timer row, allowing optional
	 * Redis mirroring of database timer state.</p>
	 *
	 * @param details timer rows to synchronize into Redis
	 */
	public void syncTimerRows(List<BookingPaymentTimerDetails> details) {
		var ttl = paymentTimerDuration();
		for (var detail : details) {
			var rowKey = PaymentTimerKeyBuilder.toRedisTimerRowKey(detail);
			var value = PaymentTimerKeyBuilder.toRedisValue(detail.getCreatedBy(), detail.getBookingId());
			redis.opsForValue().set(rowKey, value, ttl);
			log.debug("Redis timer row key synced: {}", rowKey);
		}
	}

	/**
	 * Removes Redis entries for the provided timer rows.
	 *
	 * <p>Both the row mirror and slot hold keys are deleted for each timer detail.</p>
	 *
	 * @param details timer rows whose Redis mirror entries should be removed
	 */
	public void removeTimerRows(List<BookingPaymentTimerDetails> details) {
		for (var detail : details) {
			redis.delete(PaymentTimerKeyBuilder.toRedisTimerRowKey(detail));
			redis.delete(PaymentTimerKeyBuilder.toRedisSlotKey(detail));
		}
	}

	/**
	 * Returns the configured payment timer duration for Redis TTL operations.
	 *
	 * @return duration configured for advertisement payment holds
	 */
	private Duration paymentTimerDuration() {
		return Duration.ofMillis(bookingConfiguration.getPaymentTimer());
	}
}
