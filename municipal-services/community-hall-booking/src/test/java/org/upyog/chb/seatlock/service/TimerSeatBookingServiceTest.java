package org.upyog.chb.seatlock.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.chb.seatlock.api.SeatLockService;
import org.upyog.chb.seatlock.config.SeatLockProperties;
import org.upyog.chb.seatlock.db.SeatLockIdempotencyRepository;
import org.upyog.chb.seatlock.model.BookingConfirmationResult;
import org.upyog.chb.seatlock.model.LockSeatResult;
import org.upyog.chb.seatlock.model.RateLimitDecision;
import org.upyog.chb.seatlock.model.ReleaseSeatResult;
import org.upyog.chb.seatlock.ratelimit.SeatLockRateLimiter;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimerSeatBookingService (unit)")
class TimerSeatBookingServiceTest {

	private static final Instant FIXED_LOCK_EXPIRY = Instant.parse("2030-01-01T00:01:00Z");
	private static final Duration DEFAULT_LOCK_TTL = Duration.ofMinutes(5);
	private static final Duration CUSTOM_LOCK_TTL = Duration.ofMinutes(2);

	@Mock
	private SeatLockService seatLockService;

	@Mock
	private SeatLockRateLimiter rateLimiter;

	@Mock
	private SeatLockIdempotencyRepository idempotencyRepository;

	private SeatLockProperties properties;

	private TimerSeatBookingService service;

	@BeforeEach
	void setUp() {
		properties = new SeatLockProperties("db", Duration.ofMinutes(5), "seat:", false, 5, 10, Duration.ofMinutes(2),
				20, Duration.ofMinutes(30));
		service = new TimerSeatBookingService(seatLockService, rateLimiter, properties, idempotencyRepository);
	}

	@Test
	@DisplayName("Given rate limit allows When acquireLock Then delegates to SeatLockService")
	void acquireLock_success() {
		when(rateLimiter.beforeLock("u1")).thenReturn(new RateLimitDecision.Allowed());
		when(seatLockService.lockSeat(eq("S1"), eq("u1"), any(Duration.class)))
				.thenReturn(new LockSeatResult.Acquired("S1", "u1", FIXED_LOCK_EXPIRY));
		var result = service.acquireLock("S1", "u1", Optional.of(CUSTOM_LOCK_TTL));
		assertThat(result).isInstanceOf(LockSeatResult.Acquired.class);
		verify(rateLimiter).onLockAcquired("u1");
	}

	@Test
	@DisplayName("Given rate limit denies When acquireLock Then RateLimited and no lock call")
	void acquireLock_rateLimited() {
		when(rateLimiter.beforeLock("u1")).thenReturn(new RateLimitDecision.DeniedTooManyLocks(6, 5));
		var result = service.acquireLock("S1", "u1", Optional.empty());
		assertThat(result).isInstanceOf(LockSeatResult.RateLimited.class);
		verify(seatLockService, never()).lockSeat(any(), any(), any());
		verify(rateLimiter, never()).onLockAcquired(any());
	}

	@Test
	@DisplayName("Given default ttl When acquireLock Then uses property default")
	void acquireLock_defaultTtl() {
		when(rateLimiter.beforeLock("u1")).thenReturn(new RateLimitDecision.Allowed());
		when(seatLockService.lockSeat("S1", "u1", DEFAULT_LOCK_TTL))
				.thenReturn(new LockSeatResult.Acquired("S1", "u1", FIXED_LOCK_EXPIRY));
		service.acquireLock("S1", "u1", Optional.empty());
		verify(seatLockService).lockSeat("S1", "u1", DEFAULT_LOCK_TTL);
	}

	@Test
	@DisplayName("Given first confirm When release succeeds Then Confirmed and idempotency marked")
	void confirmAfterPayment_success() {
		when(idempotencyRepository.findOutcome("pay-1")).thenReturn(Optional.empty());
		when(idempotencyRepository.insertPending("pay-1", "S1", "u1")).thenReturn(true);
		when(seatLockService.releaseSeat("S1", "u1")).thenReturn(new ReleaseSeatResult.Released("S1"));
		var result = service.confirmAfterPayment("S1", "u1", "pay-1");
		assertThat(result).isInstanceOf(BookingConfirmationResult.Confirmed.class);
		verify(idempotencyRepository).markConfirmed("pay-1");
	}

	@Test
	@DisplayName("Given duplicate idempotency key When confirm Then IdempotentReplay")
	void confirmAfterPayment_idempotent() {
		when(idempotencyRepository.findOutcome("pay-1")).thenReturn(Optional.of("CONFIRMED"));
		var result = service.confirmAfterPayment("S1", "u1", "pay-1");
		assertThat(result).isInstanceOf(BookingConfirmationResult.IdempotentReplay.class);
		verify(seatLockService, never()).releaseSeat(any(), any());
	}

	@Test
	@DisplayName("Given lock expired before confirm When release NotLocked Then LockMissingOrExpired and pending removed")
	void confirmAfterPayment_lockExpired() {
		when(idempotencyRepository.findOutcome("pay-1")).thenReturn(Optional.empty());
		when(idempotencyRepository.insertPending("pay-1", "S1", "u1")).thenReturn(true);
		when(seatLockService.releaseSeat("S1", "u1")).thenReturn(new ReleaseSeatResult.NotLocked("S1"));
		var result = service.confirmAfterPayment("S1", "u1", "pay-1");
		assertThat(result).isInstanceOf(BookingConfirmationResult.LockMissingOrExpired.class);
		verify(idempotencyRepository).deletePending("pay-1");
		verify(idempotencyRepository, never()).markConfirmed(any());
	}

	@Test
	@DisplayName("Given blank idempotency When confirm Then InvalidArgument")
	void confirmAfterPayment_invalidKey() {
		var result = service.confirmAfterPayment("S1", "u1", "  ");
		assertThat(result).isInstanceOf(BookingConfirmationResult.InvalidArgument.class);
	}
}
