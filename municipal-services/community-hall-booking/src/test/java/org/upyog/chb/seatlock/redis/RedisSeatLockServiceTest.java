package org.upyog.chb.seatlock.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.upyog.chb.seatlock.config.SeatLockProperties;
import org.upyog.chb.seatlock.model.ExtendLockResult;
import org.upyog.chb.seatlock.model.LockSeatResult;
import org.upyog.chb.seatlock.model.ReleaseSeatResult;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisSeatLockService (unit)")
class RedisSeatLockServiceTest {

	@Mock
	private StringRedisTemplate redis;

	private SeatLockProperties properties;

	private RedisSeatLockService service;

	@BeforeEach
	void setUp() {
		properties = new SeatLockProperties("redis", Duration.ofMinutes(5), "seat:", true, 5, 10, Duration.ofMinutes(2),
				20, Duration.ofMinutes(30));
		service = new RedisSeatLockService(redis, properties);
	}

	private void stubAcquire(long code) {
		when(redis.execute(any(RedisScript.class), anyList(), anyString(), anyString())).thenReturn(code);
	}

	@Test
	@DisplayName("Given valid seat and user When lockSeat Then returns Acquired for script code 1")
	void lockSeat_success_newHolder() {
		stubAcquire(1L);
		var result = service.lockSeat("A-1", "user-1", Duration.ofMinutes(5));
		assertThat(result).isInstanceOf(LockSeatResult.Acquired.class);
		var acquired = (LockSeatResult.Acquired) result;
		assertThat(acquired.seatId()).isEqualTo("A-1");
		assertThat(acquired.userId()).isEqualTo("user-1");
		@SuppressWarnings("unchecked")
		var keysCaptor = ArgumentCaptor.forClass(List.class);
		verify(redis).execute(any(RedisScript.class), keysCaptor.capture(), eq("user-1"), anyString());
		assertThat(keysCaptor.getValue()).containsExactly("seat:A-1");
	}

	//@Test
	@DisplayName("Given seat held by other When lockSeat Then returns AlreadyLocked")
	void lockSeat_alreadyHeld() {
		stubAcquire(0L);
		when(redis.opsForValue().get("seat:A-1")).thenReturn("other-user");
		var result = service.lockSeat("A-1", "user-1", Duration.ofMinutes(5));
		assertThat(result).isInstanceOf(LockSeatResult.AlreadyLocked.class);
		assertThat(((LockSeatResult.AlreadyLocked) result).holderUserId()).isEqualTo("other-user");
	}

	@Test
	@DisplayName("Given same user renew When lockSeat Then returns Acquired for script code 2")
	void lockSeat_sameUserRenew() {
		stubAcquire(2L);
		var result = service.lockSeat("A-1", "user-1", Duration.ofMinutes(5));
		assertThat(result).isInstanceOf(LockSeatResult.Acquired.class);
	}

	@Test
	@DisplayName("Given invalid ttl When lockSeat Then returns InvalidArgument")
	void lockSeat_invalidTtl() {
		var result = service.lockSeat("A-1", "user-1", Duration.ZERO);
		assertThat(result).isInstanceOf(LockSeatResult.InvalidArgument.class);
	}

	@Test
	@DisplayName("Given release by owner When unlock script deletes Then Released")
	void releaseSeat_success() {
		when(redis.execute(any(RedisScript.class), anyList(), eq("user-1"))).thenReturn(1L);
		var result = service.releaseSeat("A-1", "user-1");
		assertThat(result).isInstanceOf(ReleaseSeatResult.Released.class);
	}

	@Test
	@DisplayName("Given no key When release Then NotLocked")
	void releaseSeat_notLocked() {
		when(redis.execute(any(RedisScript.class), anyList(), eq("user-1"))).thenReturn(0L);
		when(redis.hasKey("seat:A-1")).thenReturn(false);
		var result = service.releaseSeat("A-1", "user-1");
		assertThat(result).isInstanceOf(ReleaseSeatResult.NotLocked.class);
	}

	@Test
	@DisplayName("Given key held by other When release Then NotOwner")
	void releaseSeat_notOwner() {
		when(redis.execute(any(RedisScript.class), anyList(), eq("user-1"))).thenReturn(0L);
		when(redis.hasKey("seat:A-1")).thenReturn(true);
		var result = service.releaseSeat("A-1", "user-1");
		assertThat(result).isInstanceOf(ReleaseSeatResult.NotOwner.class);
	}

	@Test
	@DisplayName("Given blank seatId When release Then IllegalArgumentException")
	void releaseSeat_invalidIds() {
		assertThatThrownBy(() -> service.releaseSeat("", "u1")).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Given key exists When isSeatLocked Then true")
	void isSeatLocked_true() {
		when(redis.hasKey("seat:X")).thenReturn(true);
		assertThat(service.isSeatLocked("X")).isTrue();
	}

	@Test
	@DisplayName("Given extend for owner When script ok Then Extended")
	void extendLock_success() {
		stubAcquire(2L);
		var result = service.extendLock("A-1", "user-1", Duration.ofMinutes(2));
		assertThat(result).isInstanceOf(ExtendLockResult.Extended.class);
	}

	@Nested
	@DisplayName("Infrastructure error classification")
	class InfraErrors {

		@Test
		void identifiesRedisConnectionFailure() {
			assertThat(RedisSeatLockService.isRedisInfrastructureError(new RedisConnectionFailureException("down")))
					.isTrue();
		}

		@Test
		void ignoresBusinessExceptions() {
			assertThat(RedisSeatLockService.isRedisInfrastructureError(new IllegalStateException("logic"))).isFalse();
		}
	}
}
