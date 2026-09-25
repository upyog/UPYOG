package org.egov.ewst.consumer;

import java.util.HashMap;

import org.egov.ewst.models.EwasteRegistrationRequest;
import org.egov.ewst.service.EwasteNotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.KafkaHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Consumer class for handling Ewaste notifications.
 * This class listens to Kafka topics for ewaste creation and update events,
 * processes the incoming records, and delegates the processing to the EwasteNotificationService.
 */
@Service
@Slf4j
public class EwasteNotificationConsumer {

	private final EwasteNotificationService notificationService1;
	private final ObjectMapper mapper;

	public EwasteNotificationConsumer(EwasteNotificationService notificationService1, ObjectMapper mapper) {
		this.notificationService1 = notificationService1;
		this.mapper = mapper;
	}

	/**
	 * Kafka listener method for ewaste creation and update topics.
	 * This method consumes records from the specified Kafka topics and processes them.
	 *
	 * @param kafkaRecord the incoming record from Kafka
	 * @param topic the topic from which the record was received
	 */
	@KafkaListener(topics = { "${ewaste.kafka.create.topic}", "${ewaste.kafka.update.topic}" })
	public void listen(final HashMap<String, Object> kafkaRecord, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		EwasteRegistrationRequest ewasteRequest = new EwasteRegistrationRequest();
		try {
			log.debug("Consuming record in Ewaste for notification: " + kafkaRecord.toString());
			ewasteRequest = mapper.convertValue(kafkaRecord, EwasteRegistrationRequest.class);
		} catch (final Exception e) {
			log.error("Error while listening to value: " + kafkaRecord + " on topic: " + topic + ": " + e);
		}

		log.info("Ewaste Application Received: " + ewasteRequest.getEwasteApplication().get(0).getRequestId());

		notificationService1.process(ewasteRequest);
	}
}
