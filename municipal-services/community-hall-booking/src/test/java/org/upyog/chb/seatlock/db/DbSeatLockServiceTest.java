package org.upyog.chb.seatlock.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.upyog.chb.seatlock.model.ExtendLockResult;
import org.upyog.chb.seatlock.model.LockSeatResult;
import org.upyog.chb.seatlock.model.ReleaseSeatResult;

@ExtendWith(MockitoExtension.class)
@DisplayName("DbSeatLockService (unit)")
class DbSeatLockServiceTest {

	private static final Instant FUTURE_EXPIRY = Instant.parse("2031-06-15T12:00:00Z");
	private static final Instant PAST_EXPIRY = Instant.parse("2020-06-15T12:00:00Z");
	private static final Instant RECENT_PAST_EXPIRY = Instant.parse("2020-06-15T11:59:00Z");

	@Mock
	private DbSeatLockRepository repository;

	private DbSeatLockService service;

	@BeforeEach
	void setUp() {
		service = new DbSeatLockService(repository);
	}

	@Test
	@DisplayName("Given no row When lockSeat Then insert and Acquired")
	void lockSeat_insertPath() {
		when(repository.findForUpdate("S1")).thenReturn(Optional.empty());
		var result = service.lockSeat("S1", "u1", Duration.ofMinutes(5));
		assertThat(result).isInstanceOf(LockSeatResult.Acquired.class);
		verify(repository).insert(eq("S1"), eq("u1"), any(Instant.class));
	}

	@Test
	@DisplayName("Given active lock by other When lockSeat Then AlreadyLocked")
	void lockSeat_conflict() {
		var future = FUTURE_EXPIRY;
		when(repository.findForUpdate("S1"))
				.thenReturn(Optional.of(new SeatLockRow("S1", "other", future, 0L)));
		var result = service.lockSeat("S1", "u1", Duration.ofMinutes(5));
		assertThat(result).isInstanceOf(LockSeatResult.AlreadyLocked.class);
	}

	@Test
	@DisplayName("Given expired lock by other When lockSeat Then takeover Acquired")
	void lockSeat_takeoverAfterExpiry() {
		var past = PAST_EXPIRY;
		when(repository.findForUpdate("S1")).thenReturn(Optional.of(new SeatLockRow("S1", "other", past, 1L)));
		var result = service.lockSeat("S1", "u1", Duration.ofMinutes(5));
		assertThat(result).isInstanceOf(LockSeatResult.Acquired.class);
		verify(repository).updateLock(eq("S1"), eq("u1"), any(Instant.class));
	}

	@Test
	@DisplayName("Given same user active lock When lockSeat Then renew Acquired")
	void lockSeat_sameUserRenew() {
		var future = FUTURE_EXPIRY;
		when(repository.findForUpdate("S1")).thenReturn(Optional.of(new SeatLockRow("S1", "u1", future, 2L)));
		var result = service.lockSeat("S1", "u1", Duration.ofMinutes(5));
		assertThat(result).isInstanceOf(LockSeatResult.Acquired.class);
		verify(repository).updateLock(eq("S1"), eq("u1"), any(Instant.class));
	}

	@Test
	@DisplayName("Given insert race When DuplicateKey Then retries and eventually AlreadyLocked")
	void lockSeat_duplicateKeyRetry() {
		when(repository.findForUpdate("S1")).thenReturn(Optional.empty());
		doThrow(new DuplicateKeyException("dup"))
				.doThrow(new DuplicateKeyException("dup"))
				.doThrow(new DuplicateKeyException("dup"))
				.when(repository).insert(any(), any(), any());
		var result = service.lockSeat("S1", "u1", Duration.ofMinutes(5));
		assertThat(result).isInstanceOf(LockSeatResult.AlreadyLocked.class);
		verify(repository, times(3)).findForUpdate("S1");
	}
	@Test
	@DisplayName("Given active owner When releaseSeat Then delete and Released")
	void releaseSeat_success() {
		var future = FUTURE_EXPIRY;
		when(repository.findForUpdate("S1")).thenReturn(Optional.of(new SeatLockRow("S1", "u1", future, 0L)));
		when(repository.deleteActiveForUser(eq("S1"), eq("u1"), any(Instant.class))).thenReturn(1);
		var result = service.releaseSeat("S1", "u1");
		assertThat(result).isInstanceOf(ReleaseSeatResult.Released.class);
	}

	@Test
	@DisplayName("Given expired lock When releaseSeat Then NotLocked")
	void releaseSeat_expired() {
		var past = RECENT_PAST_EXPIRY;
		when(repository.findForUpdate("S1")).thenReturn(Optional.of(new SeatLockRow("S1", "u1", past, 0L)));
		var result = service.releaseSeat("S1", "u1");
		assertThat(result).isInstanceOf(ReleaseSeatResult.NotLocked.class);
	}

	@Test
	@DisplayName("Given wrong user When releaseSeat Then NotOwner")
	void releaseSeat_wrongUser() {
		var future = FUTURE_EXPIRY;
		when(repository.findForUpdate("S1")).thenReturn(Optional.of(new SeatLockRow("S1", "other", future, 0L)));
		var result = service.releaseSeat("S1", "u1");
		assertThat(result).isInstanceOf(ReleaseSeatResult.NotOwner.class);
	}

	@Test
	@DisplayName("Given repository When isSeatLocked Then delegates")
	void isSeatLocked() {
		when(repository.existsActive(eq("S1"), any(Instant.class))).thenReturn(true);
		assertThat(service.isSeatLocked("S1")).isTrue();
	}

	@Test
	@DisplayName("Given owner active When extendLock Then Extended")
	void extendLock_success() {
		var future = FUTURE_EXPIRY;
		when(repository.findForUpdate("S1")).thenReturn(Optional.of(new SeatLockRow("S1", "u1", future, 0L)));
		var result = service.extendLock("S1", "u1", Duration.ofMinutes(10));
		assertThat(result).isInstanceOf(ExtendLockResult.Extended.class);
	}

	@Test
	@DisplayName("Given missing lock When extendLock Then NotLocked")
	void extendLock_missing() {
		when(repository.findForUpdate("S1")).thenReturn(Optional.empty());
		var result = service.extendLock("S1", "u1", Duration.ofMinutes(10));
		assertThat(result).isInstanceOf(ExtendLockResult.NotLocked.class);
	}

	@Test
	@DisplayName("Given invalid ids When releaseSeat Then IllegalArgumentException")
	void release_invalid() {
		assertThatThrownBy(() -> service.releaseSeat("", "u")).isInstanceOf(IllegalArgumentException.class);
	}
}
