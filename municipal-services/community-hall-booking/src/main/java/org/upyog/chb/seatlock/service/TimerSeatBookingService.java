package org.upyog.chb.seatlock.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.upyog.chb.seatlock.api.SeatLockService;
import org.upyog.chb.seatlock.config.SeatLockProperties;
import org.upyog.chb.seatlock.db.SeatLockIdempotencyRepository;
import org.upyog.chb.seatlock.model.BookingConfirmationResult;
import org.upyog.chb.seatlock.model.LockSeatResult;
import org.upyog.chb.seatlock.model.RateLimitDecision;
import org.upyog.chb.seatlock.model.ReleaseSeatResult;
import org.upyog.chb.seatlock.ratelimit.SeatLockRateLimiter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnProperty(name = "seat.lock.enabled", havingValue = "true")
public class TimerSeatBookingService {

	private final SeatLockService seatLockService;
	private final SeatLockRateLimiter rateLimiter;
	private final SeatLockProperties seatLockProperties;
	private final SeatLockIdempotencyRepository idempotencyRepository;

	public TimerSeatBookingService(SeatLockService seatLockService, SeatLockRateLimiter rateLimiter,
								   SeatLockProperties seatLockProperties, SeatLockIdempotencyRepository idempotencyRepository) {
		this.seatLockService = seatLockService;
		this.rateLimiter = rateLimiter;
		this.seatLockProperties = seatLockProperties;
		this.idempotencyRepository = idempotencyRepository;
	}

	public LockSeatResult acquireLock(String seatId, String userId, Optional<Duration> ttl) {
		RateLimitDecision decision = rateLimiter.beforeLock(userId);
		Optional<LockSeatResult> rateLimited = mapRateLimit(decision);
		if (rateLimited.isPresent()) {
			return rateLimited.get();
		}
		Duration effectiveTtl = ttl.orElse(seatLockProperties.defaultLockTtl());
		LockSeatResult lockResult = seatLockService.lockSeat(seatId, userId, effectiveTtl);
		if (lockResult instanceof LockSeatResult.Acquired) {
			rateLimiter.onLockAcquired(userId);
		}
		return lockResult;
	}

	public ReleaseSeatResult releaseLock(String seatId, String userId) {
		return seatLockService.releaseSeat(seatId, userId);
	}

	public boolean isLocked(String seatId) {
		return seatLockService.isSeatLocked(seatId);
	}

	@Transactional
	public BookingConfirmationResult confirmAfterPayment(String seatId, String userId, String idempotencyKey) {
		if (idempotencyKey == null || idempotencyKey.isBlank()) {
			return new BookingConfirmationResult.InvalidArgument("idempotencyKey required");
		}
		Optional<String> prior = idempotencyRepository.findOutcome(idempotencyKey);
		if (prior.isPresent()) {
			return new BookingConfirmationResult.IdempotentReplay(seatId, userId, idempotencyKey, prior.get());
		}

		if (!idempotencyRepository.insertPending(idempotencyKey, seatId, userId)) {
			String outcome = idempotencyRepository.findOutcome(idempotencyKey).orElse("UNKNOWN");
			return new BookingConfirmationResult.IdempotentReplay(seatId, userId, idempotencyKey, outcome);
		}

		ReleaseSeatResult release = seatLockService.releaseSeat(seatId, userId);
		if (release instanceof ReleaseSeatResult.Released) {
			idempotencyRepository.markConfirmed(idempotencyKey);
			log.info("Seat booking confirmed seatId={} userId={} idempotencyKey={}", seatId, userId, idempotencyKey);
			return new BookingConfirmationResult.Confirmed(seatId, userId, idempotencyKey, Instant.now());
		}

		idempotencyRepository.deletePending(idempotencyKey);
		if (release instanceof ReleaseSeatResult.NotLocked) {
			return new BookingConfirmationResult.LockMissingOrExpired(seatId);
		}
		if (release instanceof ReleaseSeatResult.NotOwner) {
			return new BookingConfirmationResult.NotLockOwner(seatId, userId);
		}
		throw new IllegalStateException("Unexpected release outcome: " + release);
	}

	private Optional<LockSeatResult> mapRateLimit(RateLimitDecision decision) {
		if (decision instanceof RateLimitDecision.Allowed) {
			return Optional.empty();
		} else if (decision instanceof RateLimitDecision.DeniedTooManyLocks d) {
			return Optional.of(new LockSeatResult.RateLimited(
					"Too many lock attempts (%d / %d per minute)".formatted(d.currentCount(), d.maxPerWindow())));
		} else if (decision instanceof RateLimitDecision.DeniedCooldown c) {
			return Optional.of(new LockSeatResult.RateLimited("Cooldown active until " + c.retryAfter()));
		} else if (decision instanceof RateLimitDecision.DeniedBanned b) {
			return Optional.of(new LockSeatResult.RateLimited("Temporarily banned until " + b.bannedUntil()));
		}
		throw new IllegalStateException("Unexpected rate limit decision: " + decision);
	}
}