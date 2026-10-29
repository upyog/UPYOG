package org.upyog.chb.seatlock.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.upyog.chb.seatlock.db.DbSeatLockService;
import org.upyog.chb.seatlock.model.LockSeatResult;
import org.upyog.chb.seatlock.model.ReleaseSeatResult;
import org.upyog.chb.seatlock.redis.RedisSeatLockService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompositeSeatLockService (Redis → DB fallback)")
class CompositeSeatLockServiceTest {

	private static final Instant FIXED_EXPIRY = Instant.parse("2030-06-15T12:00:30Z");
	private static final Duration LOCK_TTL = Duration.ofMinutes(1);

	@Mock
	private RedisSeatLockService redis;

	@Mock
	private DbSeatLockService db;

	private CompositeSeatLockService composite;

	@BeforeEach
	void setUp() {
		composite = new CompositeSeatLockService(redis, db);
	}

	@Test
	@DisplayName("Given Redis succeeds When lockSeat Then returns Redis outcome")
	void lockSeat_primaryRedis() {
		var acquired = new LockSeatResult.Acquired("S1", "u1", FIXED_EXPIRY);
		when(redis.lockSeat("S1", "u1", LOCK_TTL)).thenReturn(acquired);
		assertThat(composite.lockSeat("S1", "u1", LOCK_TTL)).isSameAs(acquired);
	}

	@Test
	@DisplayName("Given Redis infrastructure failure When lockSeat Then falls back to DB")
	void lockSeat_fallbackDb() {
		when(redis.lockSeat(eq("S1"), eq("u1"), any(Duration.class)))
				.thenThrow(new RedisConnectionFailureException("simulated outage"));
		var acquired = new LockSeatResult.Acquired("S1", "u1", FIXED_EXPIRY);
		when(db.lockSeat("S1", "u1", LOCK_TTL)).thenReturn(acquired);
		assertThat(composite.lockSeat("S1", "u1", LOCK_TTL)).isSameAs(acquired);
	}

	@Test
	@DisplayName("Given Redis throws non-infra error When lockSeat Then propagates")
	void lockSeat_nonInfraPropagates() {
		when(redis.lockSeat(any(), any(), any())).thenThrow(new IllegalStateException("bug"));
		assertThatThrownBy(() -> composite.lockSeat("S1", "u1", LOCK_TTL))
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	@DisplayName("Given Redis fails on release When releaseSeat Then DB handles")
	void release_fallback() {
		when(redis.releaseSeat("S1", "u1")).thenThrow(new RedisConnectionFailureException("down"));
		when(db.releaseSeat("S1", "u1")).thenReturn(new ReleaseSeatResult.Released("S1"));
		assertThat(composite.releaseSeat("S1", "u1")).isInstanceOf(ReleaseSeatResult.Released.class);
	}
}
