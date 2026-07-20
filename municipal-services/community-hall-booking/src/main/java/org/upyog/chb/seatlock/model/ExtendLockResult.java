package org.upyog.chb.seatlock.model;

import java.time.Instant;

public sealed interface ExtendLockResult permits ExtendLockResult.Extended, ExtendLockResult.NotOwner,
		ExtendLockResult.NotLocked, ExtendLockResult.InvalidArgument {

	record Extended(String seatId, Instant newExpiresAt) implements ExtendLockResult {
	}

	record NotOwner(String seatId) implements ExtendLockResult {
	}

	record NotLocked(String seatId) implements ExtendLockResult {
	}

	record InvalidArgument(String message) implements ExtendLockResult {
	}
}
