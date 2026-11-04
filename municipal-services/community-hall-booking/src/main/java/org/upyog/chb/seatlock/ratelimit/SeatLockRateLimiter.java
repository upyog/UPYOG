package org.upyog.chb.seatlock.ratelimit;

import org.upyog.chb.seatlock.model.RateLimitDecision;

/**
 * Redis-backed abuse prevention (optional no-op when disabled / Redis absent).
 */
public interface SeatLockRateLimiter {

	/**
	 * @return decision before attempting a new seat lock
	 */
	RateLimitDecision beforeLock(String userId);

	/**
	 * Record a successful lock acquisition for sliding/minute counters.
	 */
	void onLockAcquired(String userId);
}
