package org.upyog.chb.seatlock.ratelimit;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.upyog.chb.seatlock.config.SeatLockProperties;
import org.upyog.chb.seatlock.model.RateLimitDecision;

import lombok.extern.slf4j.Slf4j;

/**
 * Per-user counters in Redis: minute bucket for lock attempts, cooldown, temporary ban.
 * Keys: {@code seatlock:ban:user:{id}}, {@code seatlock:cooldown:user:{id}},
 * {@code seatlock:rate:user:{id}:min:{epochMinute}}.
 */
@Slf4j
public final class RedisSeatLockRateLimiter implements SeatLockRateLimiter {

	private static final String BAN_KEY = "seatlock:ban:user:%s";
	private static final String COOLDOWN_KEY = "seatlock:cooldown:user:%s";
	private static final String MINUTE_COUNT_KEY = "seatlock:rate:user:%s:min:%d";

	private final StringRedisTemplate redis;
	private final SeatLockProperties props;

	public RedisSeatLockRateLimiter(StringRedisTemplate redis, SeatLockProperties props) {
		this.redis = redis;
		this.props = props;
	}

	@Override
	public RateLimitDecision beforeLock(String userId) {
		var banKey = BAN_KEY.formatted(userId);
		if (Boolean.TRUE.equals(redis.hasKey(banKey))) {
			var banTtl = Math.max(1L, redis.getExpire(banKey));
			var until = Instant.now().plusSeconds(banTtl);
			return new RateLimitDecision.DeniedBanned(until);
		}

		var cdKey = COOLDOWN_KEY.formatted(userId);
		if (Boolean.TRUE.equals(redis.hasKey(cdKey))) {
			var cdTtl = Math.max(1L, redis.getExpire(cdKey));
			return new RateLimitDecision.DeniedCooldown(Instant.now().plusSeconds(cdTtl));
		}

		var minute = Instant.now().getEpochSecond() / 60;
		var countKey = MINUTE_COUNT_KEY.formatted(userId, minute);
		var count = Optional.ofNullable(redis.opsForValue().get(countKey)).map(Long::parseLong).orElse(0L);
		if (count >= props.maxLocksPerMinute()) {
			return new RateLimitDecision.DeniedTooManyLocks(count.intValue(), props.maxLocksPerMinute());
		}
		return new RateLimitDecision.Allowed();
	}

	@Override
	public void onLockAcquired(String userId) {
		var minute = Instant.now().getEpochSecond() / 60;
		var countKey = MINUTE_COUNT_KEY.formatted(userId, minute);
		Long newVal = redis.opsForValue().increment(countKey);
		if (newVal != null && newVal == 1L) {
			redis.expire(countKey, Duration.ofMinutes(2));
		}

		if (newVal != null && newVal >= props.cooldownAfterLocks() && newVal < props.banAfterLocks()) {
			var cdKey = COOLDOWN_KEY.formatted(userId);
			redis.opsForValue().setIfAbsent(cdKey, "1", props.cooldownDuration());
			log.warn("seat-lock cooldown applied userId={} locksInMinute={}", userId, newVal);
		}

		if (newVal != null && newVal >= props.banAfterLocks()) {
			var banKey = BAN_KEY.formatted(userId);
			redis.opsForValue().set(banKey, "1", props.banDuration());
			log.warn("seat-lock temporary ban applied userId={} locksInMinute={}", userId, newVal);
		}
	}
}
