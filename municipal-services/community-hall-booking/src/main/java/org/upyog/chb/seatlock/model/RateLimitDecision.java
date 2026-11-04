package org.upyog.chb.seatlock.model;

import java.time.Instant;

/**
 * Sealed rate-limit gate outcome — models cooldown / soft-ban without throwing for flow control.
 */
public sealed interface RateLimitDecision permits RateLimitDecision.Allowed, RateLimitDecision.DeniedTooManyLocks,
		RateLimitDecision.DeniedCooldown, RateLimitDecision.DeniedBanned {

	record Allowed() implements RateLimitDecision {
	}

	record DeniedTooManyLocks(int currentCount, int maxPerWindow) implements RateLimitDecision {
	}

	record DeniedCooldown(Instant retryAfter) implements RateLimitDecision {
	}

	record DeniedBanned(Instant bannedUntil) implements RateLimitDecision {
	}
}
