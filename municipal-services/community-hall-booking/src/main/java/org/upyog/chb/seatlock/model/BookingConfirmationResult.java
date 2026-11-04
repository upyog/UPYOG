package org.upyog.chb.seatlock.model;

import java.time.Instant;

/**
 * Outcome when converting an active lock into a confirmed booking (payment success path).
 */
public sealed interface BookingConfirmationResult permits BookingConfirmationResult.Confirmed,
		BookingConfirmationResult.IdempotentReplay, BookingConfirmationResult.LockMissingOrExpired,
		BookingConfirmationResult.NotLockOwner, BookingConfirmationResult.InvalidArgument {

	record Confirmed(String seatId, String userId, String idempotencyKey, Instant confirmedAt)
			implements BookingConfirmationResult {
	}

	record IdempotentReplay(String seatId, String userId, String idempotencyKey, String priorOutcome)
			implements BookingConfirmationResult {
	}

	record LockMissingOrExpired(String seatId) implements BookingConfirmationResult {
	}

	record NotLockOwner(String seatId, String userId) implements BookingConfirmationResult {
	}

	record InvalidArgument(String message) implements BookingConfirmationResult {
	}
}
