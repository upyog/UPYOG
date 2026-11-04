package org.upyog.chb.seatlock.db;

import java.time.Instant;

/**
 * Immutable DB projection for {@code seat_locks} — {@code record} keeps mapping concise and thread-safe.
 */
public record SeatLockRow(String seatId, String userId, Instant lockExpiryTime, long version) {
}
