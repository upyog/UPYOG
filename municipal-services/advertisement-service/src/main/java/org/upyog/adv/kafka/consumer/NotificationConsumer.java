package org.upyog.adv.kafka.consumer;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.upyog.adv.service.ADVNotificationService;
import org.upyog.adv.web.models.BookingRequest;
import org.springframework.kafka.support.KafkaHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * This class acts as a Kafka consumer for the Advertisement Booking Service.
 * It listens to booking-related topics and processes notifications accordingly.
 * 
 * Key responsibilities:
 * - Listens to advertisement booking creation and update events
 * - Deserializes Kafka messages into BookingRequest objects
 * - Triggers notifications based on booking status changes
 * - Handles errors during message processing
 * 
 * Dependencies:
 * - ADVNotificationService: Handles notification delivery
 * - ObjectMapper: JSON serialization/deserialization
 */

@Service
@Slf4j
public class NotificationConsumer {

	private final ADVNotificationService notificationService;

	private final ObjectMapper mapper;

	public NotificationConsumer(ADVNotificationService notificationService, ObjectMapper mapper) {
		this.notificationService = notificationService;
		this.mapper = mapper;
	}

	@KafkaListener(topics = { "${persister.save.advertisement.booking.topic}",
			"${persister.update.advertisement.booking.topic}" })
	public void listen(final Map<String, Object> messagePayload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		BookingRequest bookingRequest = new BookingRequest();
		try {

			log.info("Consuming record in ADV for notification: " + messagePayload.toString() + " from topic: " + topic);
			bookingRequest = mapper.convertValue(messagePayload, BookingRequest.class);
		} catch (final Exception e) {
			log.error("Error while processing CHB notification to value: " + messagePayload + " on topic: " + topic + ": " + e);
		}

		String bookingStatus = bookingRequest.getBookingApplication().getBookingStatus();
		log.info("ADV Appplication Received with booking no : " + bookingRequest.getBookingApplication().getBookingNo()
				+ " and for status : " + bookingStatus);

		notificationService.process(bookingRequest, bookingStatus);
	}
}
