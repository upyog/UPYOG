package org.upyog.chb.seatlock.ratelimit;

import org.upyog.chb.seatlock.model.RateLimitDecision;

/**
 * Installed when rate limiting is disabled or Redis is unavailable.
 */
public final class NoOpSeatLockRateLimiter implements SeatLockRateLimiter {

	@Override
	public RateLimitDecision beforeLock(String userId) {
		return new RateLimitDecision.Allowed();
	}

	@Override
	public void onLockAcquired(String userId) {
		// no-op
	}
}
