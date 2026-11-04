package org.upyog.chb.seatlock.db;

import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Persists idempotency keys for payment / booking callbacks (survives process restarts).
 */
@Repository
public class SeatLockIdempotencyRepository {

	private final JdbcTemplate jdbc;

	public SeatLockIdempotencyRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public Optional<String> findOutcome(String idempotencyKey) {
		try {
			var o = jdbc.queryForObject("SELECT outcome FROM seat_lock_idempotency WHERE idempotency_key = ?",
					String.class, idempotencyKey);
			return Optional.ofNullable(o);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	/**
	 * @return true if this call created the pending row
	 */
	public boolean insertPending(String idempotencyKey, String seatId, String userId) {
		var inserted = jdbc.update(
				"INSERT INTO seat_lock_idempotency (idempotency_key, seat_id, user_id, outcome) VALUES (?,?,?, 'PENDING') ON CONFLICT (idempotency_key) DO NOTHING",
				idempotencyKey, seatId, userId);
		return inserted == 1;
	}

	public void markConfirmed(String idempotencyKey) {
		jdbc.update("UPDATE seat_lock_idempotency SET outcome = 'CONFIRMED' WHERE idempotency_key = ?",
				idempotencyKey);
	}

	public void deletePending(String idempotencyKey) {
		jdbc.update("DELETE FROM seat_lock_idempotency WHERE idempotency_key = ? AND outcome = 'PENDING'",
				idempotencyKey);
	}
}
