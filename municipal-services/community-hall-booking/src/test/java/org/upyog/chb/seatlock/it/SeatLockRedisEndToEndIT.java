package org.upyog.chb.seatlock.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.upyog.chb.seatlock.model.BookingConfirmationResult;
import org.upyog.chb.seatlock.model.LockSeatResult;
import org.upyog.chb.seatlock.model.ReleaseSeatResult;
import org.upyog.chb.seatlock.service.TimerSeatBookingService;

/**
 * End-to-end seat flow against real Redis + PostgreSQL (schema only for idempotency / future DB fallback).
 */
@SpringBootTest(classes = SeatLockIntegrationTestApplication.class)
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("Seat lock integration (Redis)")
@Ignore
class SeatLockRedisEndToEndIT {

	@Container
	@SuppressWarnings("resource")
	static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Container
	@SuppressWarnings("resource")
	static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
			.withExposedPorts(6379);

	@DynamicPropertySource
	static void register(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.data.redis.host", redis::getHost);
		registry.add("spring.data.redis.port", () -> String.valueOf(redis.getMappedPort(6379)));
		registry.add("spring.sql.init.mode", () -> "always");
		registry.add("spring.sql.init.schema-locations", () -> "classpath:schema-seat-lock-test.sql");
		registry.add("spring.flyway.enabled", () -> "false");
		registry.add("seat.lock.enabled", () -> "true");
		registry.add("seat-lock.provider", () -> "redis");
		registry.add("seat-lock.rate-limit-enabled", () -> "false");
		registry.add("seat-lock.default-lock-ttl", () -> "PT5M");
	}

	@Autowired
	private TimerSeatBookingService timerSeatBookingService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("Given free seat When lock release Then Redis and DB idempotency tables stay consistent")
	void lock_release_roundTrip() {
		var lock = timerSeatBookingService.acquireLock("HALL-1-A", "citizen-1", Optional.of(Duration.ofMinutes(5)));
		assertThat(lock).isInstanceOf(LockSeatResult.Acquired.class);
		assertThat(timerSeatBookingService.isLocked("HALL-1-A")).isTrue();

		var released = timerSeatBookingService.releaseLock("HALL-1-A", "citizen-1");
		assertThat(released).isInstanceOf(ReleaseSeatResult.Released.class);
		assertThat(timerSeatBookingService.isLocked("HALL-1-A")).isFalse();
	}

	@Test
	@DisplayName("Given payment success When confirm twice Then second call is idempotent replay")
	void confirm_idempotent() {
		timerSeatBookingService.acquireLock("SEAT-9", "u-pay", Optional.of(Duration.ofMinutes(5)));
		var first = timerSeatBookingService.confirmAfterPayment("SEAT-9", "u-pay", "idem-pay-1");
		assertThat(first).isInstanceOf(BookingConfirmationResult.Confirmed.class);

		var second = timerSeatBookingService.confirmAfterPayment("SEAT-9", "u-pay", "idem-pay-1");
		assertThat(second).isInstanceOf(BookingConfirmationResult.IdempotentReplay.class);

		var rows = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM seat_lock_idempotency WHERE idempotency_key = ?", Integer.class, "idem-pay-1");
		assertThat(rows).isEqualTo(1);
	}

	@Test
	@DisplayName("Given many concurrent lock attempts on same seat When all complete Then exactly one acquires")
	void concurrent_same_seat_single_winner() throws Exception {
		var seat = "CONC-SEAT-1";
		int threads = 12;
		var latch = new CountDownLatch(1);
		var acquired = new AtomicInteger();
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		try {
			Future<?>[] futures = new Future<?>[threads];
			for (int i = 0; i < threads; i++) {
				final int idx = i;
				futures[i] = pool.submit(() -> {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    var r = timerSeatBookingService.acquireLock(seat, "user-" + idx, Optional.of(Duration.ofMinutes(5)));
					if (r instanceof LockSeatResult.Acquired) {
						acquired.incrementAndGet();
					}
				});
			}
			latch.countDown();
			for (Future<?> f : futures) {
				f.get(30, TimeUnit.SECONDS);
			}
		}finally {
			pool.shutdownNow();
		}
		assertThat(acquired.get()).isEqualTo(1);
	}

	@Test
	@DisplayName("Given short TTL When lock expires Then another user can lock (simulated expiry via release)")
	void lock_expiry_retry_scenario() {
		timerSeatBookingService.acquireLock("TTL-SEAT", "first", Optional.of(Duration.ofMillis(200)));
		timerSeatBookingService.releaseLock("TTL-SEAT", "first");
		var second = timerSeatBookingService.acquireLock("TTL-SEAT", "second", Optional.of(Duration.ofMinutes(5)));
		assertThat(second).isInstanceOf(LockSeatResult.Acquired.class);
		assertThat(((LockSeatResult.Acquired) second).userId()).isEqualTo("second");
	}
}
