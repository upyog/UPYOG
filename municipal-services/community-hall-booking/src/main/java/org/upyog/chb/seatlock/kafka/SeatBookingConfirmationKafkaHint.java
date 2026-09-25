package org.upyog.chb.seatlock.kafka;

/**
 * Integration hint (no transport wiring here): after {@code confirmAfterPayment} succeeds, publish an
 * outbox event such as {@code seat.booking.confirmed} so downstream inventory, analytics, or notification
 * consumers react asynchronously. Use the transactional outbox pattern: write to an {@code outbox_events}
 * table in the same DB transaction as {@code markConfirmed}, then a relay process publishes to Kafka — this
 * avoids double-send on HTTP retries and survives broker outages.
 */
public final class SeatBookingConfirmationKafkaHint {

	private SeatBookingConfirmationKafkaHint() {
	}
}
