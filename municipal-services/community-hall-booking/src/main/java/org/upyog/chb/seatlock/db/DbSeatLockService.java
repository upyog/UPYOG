package org.upyog.chb.seatlock.db;

import java.time.Duration;
import java.time.Instant;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.upyog.chb.seatlock.api.SeatLockService;
import org.upyog.chb.seatlock.model.ExtendLockResult;
import org.upyog.chb.seatlock.model.LockSeatResult;
import org.upyog.chb.seatlock.model.ReleaseSeatResult;

import lombok.extern.slf4j.Slf4j;

/**
 * PostgreSQL-backed locks with {@code SELECT ... FOR UPDATE} to serialize concurrent booking attempts.
 */
@Slf4j
public class DbSeatLockService implements SeatLockService {

	private final DbSeatLockRepository repo;

	public DbSeatLockService(DbSeatLockRepository repo) {
		this.repo = repo;
	}

	@Override
	@Transactional
	public LockSeatResult lockSeat(String seatId, String userId, Duration ttl) {
		var v = validate(seatId, userId, ttl);
		if (v != null) {
			return v;
		}
		var now = Instant.now();
		var newExpiry = now.plus(ttl);
		for (var attempt = 0; attempt < 3; attempt++) {
			try {
				var row = repo.findForUpdate(seatId);
				if (row.isEmpty()) {
					repo.insert(seatId, userId, newExpiry);
					return new LockSeatResult.Acquired(seatId, userId, newExpiry);
				}
				var r = row.get();
				var expired = !r.lockExpiryTime().isAfter(now);
				if (!expired && !r.userId().equals(userId)) {
					return new LockSeatResult.AlreadyLocked(seatId, r.userId());
				}
				repo.updateLock(seatId, userId, newExpiry);
				return new LockSeatResult.Acquired(seatId, userId, newExpiry);
			} catch (DuplicateKeyException e) {
				log.info("seat lock insert race seatId={} attempt={}", seatId, attempt);
			}
		}
		return new LockSeatResult.AlreadyLocked(seatId, "unknown");
	}

	@Override
	@Transactional
	public ReleaseSeatResult releaseSeat(String seatId, String userId) {
		requireIds(seatId, userId);
		var now = Instant.now();
		var row = repo.findForUpdate(seatId);
		if (row.isEmpty()) {
			return new ReleaseSeatResult.NotLocked(seatId);
		}
		var r = row.get();
		if (!r.userId().equals(userId)) {
			return new ReleaseSeatResult.NotOwner(seatId, userId);
		}
		if (!r.lockExpiryTime().isAfter(now)) {
			return new ReleaseSeatResult.NotLocked(seatId);
		}
		var deleted = repo.deleteActiveForUser(seatId, userId, now);
		return deleted > 0 ? new ReleaseSeatResult.Released(seatId) : new ReleaseSeatResult.NotLocked(seatId);
	}

	@Override
	public boolean isSeatLocked(String seatId) {
		if (seatId == null || seatId.isBlank()) {
			return false;
		}
		return repo.existsActive(seatId, Instant.now());
	}

	@Override
	@Transactional
	public ExtendLockResult extendLock(String seatId, String userId, Duration ttl) {
		var v = validate(seatId, userId, ttl);
		if (v != null) {
			if (v instanceof LockSeatResult.InvalidArgument invalidArgument) {
				return new ExtendLockResult.InvalidArgument(invalidArgument.message());
			} else if (v instanceof LockSeatResult.RateLimited rateLimited) {
				return new ExtendLockResult.InvalidArgument(rateLimited.message());
			} else if (v instanceof LockSeatResult.Acquired || v instanceof LockSeatResult.AlreadyLocked) {
				return new ExtendLockResult.InvalidArgument("unexpected validation outcome");
			}
		}
		var now = Instant.now();
		var newExpiry = now.plus(ttl);
		var row = repo.findForUpdate(seatId);
		if (row.isEmpty()) {
			return new ExtendLockResult.NotLocked(seatId);
		}
		var r = row.get();
		if (!r.userId().equals(userId)) {
			return new ExtendLockResult.NotOwner(seatId);
		}
		if (!r.lockExpiryTime().isAfter(now)) {
			return new ExtendLockResult.NotLocked(seatId);
		}
		repo.updateLock(seatId, userId, newExpiry);
		return new ExtendLockResult.Extended(seatId, newExpiry);
	}

	private LockSeatResult validate(String seatId, String userId, Duration ttl) {
		if (seatId == null || seatId.isBlank() || userId == null || userId.isBlank()) {
			return new LockSeatResult.InvalidArgument("seatId and userId required");
		}
		if (ttl == null || ttl.isNegative() || ttl.isZero()) {
			return new LockSeatResult.InvalidArgument("ttl must be positive");
		}
		return null;
	}

	private void requireIds(String seatId, String userId) {
		if (seatId == null || seatId.isBlank() || userId == null || userId.isBlank()) {
			throw new IllegalArgumentException("seatId and userId required");
		}
	}
}