package org.upyog.chb.seatlock.model;

/**
 * Result of an unlock attempt (owner-only semantics).
 */
public sealed interface ReleaseSeatResult permits ReleaseSeatResult.Released, ReleaseSeatResult.NotOwner,
		ReleaseSeatResult.NotLocked {

	record Released(String seatId) implements ReleaseSeatResult {
	}

	record NotOwner(String seatId, String userId) implements ReleaseSeatResult {
	}

	record NotLocked(String seatId) implements ReleaseSeatResult {
	}
}
