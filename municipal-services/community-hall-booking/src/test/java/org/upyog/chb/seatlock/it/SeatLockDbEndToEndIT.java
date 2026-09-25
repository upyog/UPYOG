package org.upyog.chb.seatlock.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.upyog.chb.seatlock.model.BookingConfirmationResult;
import org.upyog.chb.seatlock.model.LockSeatResult;
import org.upyog.chb.seatlock.model.ReleaseSeatResult;
import org.upyog.chb.seatlock.service.TimerSeatBookingService;

/**
 * DB-backed seat lock integration (Redis autoconfiguration excluded).
 */
@SpringBootTest(classes = SeatLockIntegrationTestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
@org.springframework.test.context.TestPropertySource(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
		"seat.lock.enabled=true", "seat-lock.provider=db", "seat-lock.rate-limit-enabled=false" })
@DisplayName("Seat lock integration (PostgreSQL)")
@Ignore
class SeatLockDbEndToEndIT {

	@Container
	@SuppressWarnings("resource")
	static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@DynamicPropertySource
	static void register(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.sql.init.mode", () -> "always");
		registry.add("spring.sql.init.schema-locations", () -> "classpath:schema-seat-lock-test.sql");
		registry.add("spring.flyway.enabled", () -> "false");
	}

	@Autowired
	private TimerSeatBookingService timerSeatBookingService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("Given DB lock When two users compete Then second gets AlreadyLocked")
	void double_booking_prevention() {
		var first = timerSeatBookingService.acquireLock("DB-SEAT-1", "alice", Optional.of(Duration.ofHours(1)));
		assertThat(first).isInstanceOf(LockSeatResult.Acquired.class);
		var second = timerSeatBookingService.acquireLock("DB-SEAT-1", "bob", Optional.of(Duration.ofHours(1)));
		assertThat(second).isInstanceOf(LockSeatResult.AlreadyLocked.class);

		var cnt = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM seat_locks WHERE seat_id = ?", Integer.class,
				"DB-SEAT-1");
		assertThat(cnt).isEqualTo(1);
	}

	@Test
	@DisplayName("Given active DB lock When owner confirms Then row removed and idempotency CONFIRMED")
	void confirm_clears_lock_row() {
		timerSeatBookingService.acquireLock("DB-SEAT-2", "owner", Optional.of(Duration.ofHours(1)));
		var confirm = timerSeatBookingService.confirmAfterPayment("DB-SEAT-2", "owner", "pay-db-1");
		assertThat(confirm).isInstanceOf(BookingConfirmationResult.Confirmed.class);

		var active = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM seat_locks WHERE seat_id = ? AND lock_expiry_time > NOW()", Integer.class,
				"DB-SEAT-2");
		assertThat(active).isZero();

		var outcome = jdbcTemplate.queryForObject("SELECT outcome FROM seat_lock_idempotency WHERE idempotency_key = ?",
				String.class, "pay-db-1");
		assertThat(outcome).isEqualTo("CONFIRMED");
	}

	@Test
	@DisplayName("Given wrong user When release Then NotOwner and row remains")
	void release_wrong_user() {
		timerSeatBookingService.acquireLock("DB-SEAT-3", "owner", Optional.of(Duration.ofHours(1)));
		var bad = timerSeatBookingService.releaseLock("DB-SEAT-3", "intruder");
		assertThat(bad).isInstanceOf(ReleaseSeatResult.NotOwner.class);
		assertThat(timerSeatBookingService.isLocked("DB-SEAT-3")).isTrue();
	}
}
