package org.upyog.chb.seatlock.api;

	import java.time.Duration;

	import org.upyog.chb.seatlock.model.ExtendLockResult;
	import org.upyog.chb.seatlock.model.LockSeatResult;
	import org.upyog.chb.seatlock.model.ReleaseSeatResult;

	/**
	 * Interface for managing seat locks with pluggable implementations (e.g., Redis, DB).
	 * Provides methods for locking, releasing, checking, and extending seat locks.
	 */
	public interface SeatLockService {

	    /**
	     * Locks a seat for a specific user for a given duration.
	     *
	     * @param seatId The unique identifier of the seat to lock.
	     * @param userId The identifier of the user requesting the lock.
	     * @param ttl The time-to-live (TTL) duration for the lock.
	     * @return The result of the lock operation.
	     */
	    LockSeatResult lockSeat(String seatId, String userId, Duration ttl);

	    /**
	     * Releases a previously locked seat for a specific user.
	     *
	     * @param seatId The unique identifier of the seat to release.
	     * @param userId The identifier of the user requesting the release.
	     * @return The result of the release operation.
	     */
	    ReleaseSeatResult releaseSeat(String seatId, String userId);

	    /**
	     * Checks if a seat is currently locked.
	     *
	     * @param seatId The unique identifier of the seat to check.
	     * @return True if the seat is locked, false otherwise.
	     */
	    boolean isSeatLocked(String seatId);

	    /**
	     * Extends the lock duration for a specific seat.
	     *
	     * @param seatId The unique identifier of the seat to extend the lock for.
	     * @param userId The identifier of the user requesting the lock extension.
	     * @param ttl The new time-to-live (TTL) duration for the lock.
	     * @return The result of the lock extension operation.
	     */
	    ExtendLockResult extendLock(String seatId, String userId, Duration ttl);
	}