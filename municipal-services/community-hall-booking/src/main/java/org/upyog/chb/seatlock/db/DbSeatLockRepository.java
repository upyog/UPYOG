package org.upyog.chb.seatlock.db;

import java.time.Instant;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
/**
 * Row-level locking for seat acquisition / release (PostgreSQL {@code FOR UPDATE}).
 * Callers must run inside a surrounding transaction (see {@link DbSeatLockService}).
 */
@Repository
public class DbSeatLockRepository {

	private static final RowMapper<SeatLockRow> ROW = (rs, i) -> new SeatLockRow(rs.getString("seat_id"),
			rs.getString("user_id"), rs.getObject("lock_expiry_time", Instant.class), rs.getLong("version"));

	private final JdbcTemplate jdbc;

	public DbSeatLockRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public Optional<SeatLockRow> findForUpdate(String seatId) {
		var rows = jdbc.query(
				"SELECT seat_id, user_id, lock_expiry_time, version FROM seat_locks WHERE seat_id = ? FOR UPDATE",
				ROW, seatId);
		return rows.stream().findFirst();
	}

	public void insert(String seatId, String userId, Instant expiry) {
		jdbc.update(
				"INSERT INTO seat_locks (seat_id, user_id, lock_expiry_time, version) VALUES (?,?,?,0)",
				seatId, userId, expiry);
	}

	public void updateLock(String seatId, String userId, Instant expiry) {
		jdbc.update(
				"UPDATE seat_locks SET user_id = ?, lock_expiry_time = ?, version = version + 1, updated_at = NOW() WHERE seat_id = ?",
				userId, expiry, seatId);
	}

	public int deleteActiveForUser(String seatId, String userId, Instant now) {
		return jdbc.update(
				"DELETE FROM seat_locks WHERE seat_id = ? AND user_id = ? AND lock_expiry_time > ?",
				seatId, userId, now);
	}

	public boolean existsActive(String seatId, Instant now) {
		var n = jdbc.queryForObject(
				"SELECT COUNT(*) FROM seat_locks WHERE seat_id = ? AND lock_expiry_time > ?",
				Integer.class, seatId, now);
		return n != null && n > 0;
	}
}
