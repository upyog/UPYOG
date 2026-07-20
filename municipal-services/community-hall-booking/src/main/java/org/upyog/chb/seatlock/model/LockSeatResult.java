package org.upyog.chb.seatlock.model;

import java.time.Instant;

/**
 * Sealed outcome of a lock attempt — exhaustive switch in callers keeps all states explicit.
 * - Acquired: This record is returned when a seat lock is successfully acquired. It contains the seat ID, the user ID of the person who acquired the lock, and the expiration time of the lock. This indicates that the seat is now reserved for the user until the specified expiration time.
 * - AlreadyLocked: This record is returned when an attempt to lock a seat fails because the seat is already held by another user and the TTL (time-to-live) has not expired. It contains the seat ID and the user ID of the current holder of the lock. This indicates that the seat cannot be locked by the requesting user at this time.
 * - InvalidArgument: This record is returned when the lock attempt fails due to invalid input parameters, such as a non-existent seat ID or an invalid user ID. It contains a message describing the reason for the failure. This indicates that the request was malformed and needs to be corrected before retrying.
 * - RateLimited: This record is returned when the lock attempt fails due to rate limiting, which occurs when a user or IP address exceeds the allowed number of lock attempts within a certain time frame. It contains a message describing the reason for the failure. This indicates that the user should wait before attempting to lock a seat again to avoid further rate limiting.
 *
 */
public sealed interface LockSeatResult permits LockSeatResult.Acquired, LockSeatResult.AlreadyLocked,
		LockSeatResult.InvalidArgument, LockSeatResult.RateLimited {

	record Acquired(String seatId, String userId, Instant expiresAt) implements LockSeatResult {
	}

	/**
	 * Seat is held by another user and TTL has not expired.
	 */
	record AlreadyLocked(String seatId, String holderUserId) implements LockSeatResult {
	}

	record InvalidArgument(String message) implements LockSeatResult {
	}

	record RateLimited(String message) implements LockSeatResult {
	}
}
