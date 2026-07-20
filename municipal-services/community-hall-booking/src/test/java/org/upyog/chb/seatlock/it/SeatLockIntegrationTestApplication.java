package org.upyog.chb.seatlock.it;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.upyog.chb.seatlock.config.SeatLockConfiguration;

/**
 * Minimal Spring context for seat-lock integration tests (no full CHB / Kafka / encryption wiring).
 */
@SpringBootApplication(exclude = { KafkaAutoConfiguration.class })
@ComponentScan(basePackages = "org.upyog.chb.seatlock")
@Import(SeatLockConfiguration.class)
public class SeatLockIntegrationTestApplication {
}
