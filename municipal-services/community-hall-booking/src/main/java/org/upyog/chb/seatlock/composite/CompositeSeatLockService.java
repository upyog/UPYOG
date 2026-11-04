package org.upyog.chb.seatlock.composite;

import java.time.Duration;

import org.upyog.chb.seatlock.api.SeatLockService;
import org.upyog.chb.seatlock.db.DbSeatLockService;
import org.upyog.chb.seatlock.model.ExtendLockResult;
import org.upyog.chb.seatlock.model.LockSeatResult;
import org.upyog.chb.seatlock.model.ReleaseSeatResult;
import org.upyog.chb.seatlock.redis.RedisSeatLockService;

import lombok.extern.slf4j.Slf4j;

/**
 * Composite implementation of SeatLockService with Redis as the primary mechanism
 * and PostgreSQL as a fallback in case of Redis failures.
 */
@Slf4j
public class CompositeSeatLockService implements SeatLockService {

	private final RedisSeatLockService redis;
	private final DbSeatLockService db;

	public CompositeSeatLockService(RedisSeatLockService redis, DbSeatLockService db) {
		this.redis = redis;
		this.db = db;
	}

	@Override
	public LockSeatResult lockSeat(String seatId, String userId, Duration ttl) {
		try {
			// Attempt to lock the seat using Redis
			return redis.lockSeat(seatId, userId, ttl);
		} catch (RuntimeException ex) {
			// Fallback to DB if Redis fails due to infrastructure issues
			if (RedisSeatLockService.isRedisInfrastructureError(ex)) {
				log.warn("Redis seat lock failed; falling back to DB. seatId={}", seatId, ex);
				return db.lockSeat(seatId, userId, ttl);
			}
			// Rethrow the exception if it's not related to Redis infrastructure
			throw ex;
		}
	}

	@Override
	public ReleaseSeatResult releaseSeat(String seatId, String userId) {
		try {
			// Attempt to release the seat using Redis
			return redis.releaseSeat(seatId, userId);
		} catch (RuntimeException ex) {
			// Fallback to DB if Redis fails due to infrastructure issues
			if (RedisSeatLockService.isRedisInfrastructureError(ex)) {
				log.warn("Redis release failed; falling back to DB. seatId={}", seatId, ex);
				return db.releaseSeat(seatId, userId);
			}
			// Rethrow the exception if it's not related to Redis infrastructure
			throw ex;
		}
	}

	@Override
	public boolean isSeatLocked(String seatId) {
		try {
			// Check if the seat is locked using Redis
			return redis.isSeatLocked(seatId);
		} catch (RuntimeException ex) {
			// Fallback to DB if Redis fails due to infrastructure issues
			if (RedisSeatLockService.isRedisInfrastructureError(ex)) {
				return db.isSeatLocked(seatId);
			}
			// Rethrow the exception if it's not related to Redis infrastructure
			throw ex;
		}
	}

	@Override
	public ExtendLockResult extendLock(String seatId, String userId, Duration ttl) {
		try {
			// Attempt to extend the lock using Redis
			return redis.extendLock(seatId, userId, ttl);
		} catch (RuntimeException ex) {
			// Fallback to DB if Redis fails due to infrastructure issues
			if (RedisSeatLockService.isRedisInfrastructureError(ex)) {
				log.warn("Redis extend failed; falling back to DB. seatId={}", seatId, ex);
				return db.extendLock(seatId, userId, ttl);
			}
			// Rethrow the exception if it's not related to Redis infrastructure
			throw ex;
		}
	}
}