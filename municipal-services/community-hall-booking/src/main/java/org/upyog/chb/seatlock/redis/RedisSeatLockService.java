package org.upyog.chb.seatlock.redis;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.upyog.chb.seatlock.api.SeatLockService;
import org.upyog.chb.seatlock.config.SeatLockProperties;
import org.upyog.chb.seatlock.model.ExtendLockResult;
import org.upyog.chb.seatlock.model.LockSeatResult;
import org.upyog.chb.seatlock.model.ReleaseSeatResult;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of SeatLockService using Redis as the underlying mechanism.
 * Provides methods to lock, release, check, and extend seat locks.
 * Uses Redis scripts for atomic operations and ensures high performance.
 */
@Slf4j
public class RedisSeatLockService implements SeatLockService {

	private final StringRedisTemplate redis;
	private final SeatLockProperties props;
	private final DefaultRedisScript<Long> acquireScript;
	private final DefaultRedisScript<Long> unlockScript;

	/**
	 * Constructor for RedisSeatLockService.
	 *
	 * @param redis  The StringRedisTemplate instance for Redis operations.
	 * @param props  The SeatLockProperties containing configuration values.
	 */
	public RedisSeatLockService(StringRedisTemplate redis, SeatLockProperties props) {
		this.redis = redis;
		this.props = props;
		this.acquireScript = new DefaultRedisScript<>();
		this.acquireScript.setScriptText(RedisSeatLockScripts.ACQUIRE_OR_RENEW);
		this.acquireScript.setResultType(Long.class);
		this.unlockScript = new DefaultRedisScript<>();
		this.unlockScript.setScriptText(RedisSeatLockScripts.UNLOCK_IF_OWNER);
		this.unlockScript.setResultType(Long.class);
	}

	/**
	 * Attempts to lock a seat for a specific user for a given duration.
	 *
	 * @param seatId The unique identifier of the seat to lock.
	 * @param userId The identifier of the user requesting the lock.
	 * @param ttl    The time-to-live (TTL) duration for the lock.
	 * @return The result of the lock operation.
	 */
	@Override
	public LockSeatResult lockSeat(String seatId, String userId, Duration ttl) {
		Optional<LockSeatResult> validationResult = validate(seatId, userId, ttl);
		if (validationResult.isPresent()) {
			return validationResult.get();
		}
		String key = redisKey(seatId);
		long ttlMs = ttl.toMillis();
		Long code = redis.execute(acquireScript, List.of(key), userId, String.valueOf(ttlMs));
		long resultCode = (code == null) ? 0L : code;
		if (resultCode == 1L || resultCode == 2L) {
			return new LockSeatResult.Acquired(seatId, userId, Instant.now().plusMillis(ttlMs));
		} else if (resultCode == 0L) {
			return new LockSeatResult.AlreadyLocked(seatId, readHolder(key).orElse("unknown"));
		} else {
			return new LockSeatResult.InvalidArgument("Unexpected script result: " + code);
		}
	}

	/**
	 * Releases a previously locked seat for a specific user.
	 *
	 * @param seatId The unique identifier of the seat to release.
	 * @param userId The identifier of the user requesting the release.
	 * @return The result of the release operation.
	 */
	@Override
	public ReleaseSeatResult releaseSeat(String seatId, String userId) {
		requireIds(seatId, userId);
		String key = redisKey(seatId);
		Long deleted = redis.execute(unlockScript, List.of(key), userId);
		long deletedCount = deleted == null ? 0L : deleted;
		if (deletedCount > 0) {
			return new ReleaseSeatResult.Released(seatId);
		}
		if (Boolean.FALSE.equals(redis.hasKey(key))) {
			return new ReleaseSeatResult.NotLocked(seatId);
		}
		return new ReleaseSeatResult.NotOwner(seatId, userId);
	}

	/**
	 * Checks if a seat is currently locked.
	 *
	 * @param seatId The unique identifier of the seat to check.
	 * @return True if the seat is locked, false otherwise.
	 */
	@Override
	public boolean isSeatLocked(String seatId) {
		if (seatId == null || seatId.isBlank()) {
			return false;
		}
		return Boolean.TRUE.equals(redis.hasKey(redisKey(seatId)));
	}

	/**
	 * Extends the lock duration for a specific seat.
	 *
	 * @param seatId The unique identifier of the seat to extend the lock for.
	 * @param userId The identifier of the user requesting the lock extension.
	 * @param ttl    The new time-to-live (TTL) duration for the lock.
	 * @return The result of the lock extension operation.
	 */
	@Override
	public ExtendLockResult extendLock(String seatId, String userId, Duration ttl) {
		Optional<LockSeatResult> validationResult = validate(seatId, userId, ttl);
		if (validationResult.isPresent()) {
			LockSeatResult result = validationResult.get();
			if (result instanceof LockSeatResult.InvalidArgument invalidArgument) {
				return new ExtendLockResult.InvalidArgument(invalidArgument.message());
			} else if (result instanceof LockSeatResult.RateLimited rateLimited) {
				return new ExtendLockResult.InvalidArgument(rateLimited.message());
			} else {
				return new ExtendLockResult.InvalidArgument("Unexpected validation branch");
			}
		}
		String key = redisKey(seatId);
		long ttlMs = ttl.toMillis();
		Long code = redis.execute(acquireScript, List.of(key), userId, String.valueOf(ttlMs));
		long resultCode = (code == null) ? 0L : code;
		if (resultCode == 1L || resultCode == 2L) {
			return new ExtendLockResult.Extended(seatId, Instant.now().plusMillis(ttlMs));
		} else if (resultCode == 0L) {
			return new ExtendLockResult.NotOwner(seatId);
		} else {
			return new ExtendLockResult.InvalidArgument("Unexpected script result: " + code);
		}
	}

	/**
	 * Determines if the given exception is caused by Redis infrastructure issues.
	 *
	 * @param t The exception to check.
	 * @return True if the exception is related to Redis infrastructure, false otherwise.
	 */
	public static boolean isRedisInfrastructureError(Throwable t) {
		return t instanceof RedisConnectionFailureException || t instanceof RedisSystemException
				|| t instanceof QueryTimeoutException;
	}

	/**
	 * Generates the Redis key for a given seat ID.
	 *
	 * @param seatId The seat ID.
	 * @return The Redis key.
	 */
	private String redisKey(String seatId) {
		return props.redisKeyPrefix() + seatId;
	}

	/**
	 * Validates the input parameters for seat locking or extension.
	 *
	 * @param seatId The seat ID.
	 * @param userId The user ID.
	 * @param ttl    The time-to-live duration.
	 * @return An Optional containing a validation result, or empty if valid.
	 */
	private Optional<LockSeatResult> validate(String seatId, String userId, Duration ttl) {
		if (seatId == null || seatId.isBlank() || userId == null || userId.isBlank()) {
			return Optional.of(new LockSeatResult.InvalidArgument("seatId and userId required"));
		}
		if (ttl == null || ttl.isNegative() || ttl.isZero()) {
			return Optional.of(new LockSeatResult.InvalidArgument("ttl must be positive"));
		}
		return Optional.empty();
	}

	/**
	 * Ensures that the seat ID and user ID are not null or blank.
	 *
	 * @param seatId The seat ID.
	 * @param userId The user ID.
	 */
	private void requireIds(String seatId, String userId) {
		if (seatId == null || seatId.isBlank() || userId == null || userId.isBlank()) {
			throw new IllegalArgumentException("seatId and userId required");
		}
	}

	/**
	 * Reads the current holder of the lock for a given Redis key.
	 *
	 * @param key The Redis key.
	 * @return An Optional containing the holder ID, or empty if not found.
	 */
	private Optional<String> readHolder(String key) {
		return Optional.ofNullable(redis.opsForValue().get(key));
	}
}