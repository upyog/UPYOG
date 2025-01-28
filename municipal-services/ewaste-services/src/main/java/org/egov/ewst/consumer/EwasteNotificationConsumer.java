package org.egov.ewst.consumer;

import java.util.HashMap;

import org.egov.ewst.models.EwasteRegistrationRequest;
import org.egov.ewst.service.EwasteNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.KafkaHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EwasteNotificationConsumer {

	@Autowired
	private EwasteNotificationService notificationService1;

	@Autowired
	private ObjectMapper mapper;

	@KafkaListener(topics = { "${ewaste.kafka.create.topic}", "${ewaste.kafka.update.topic}" })
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		EwasteRegistrationRequest ewasteRequest = new EwasteRegistrationRequest();
		try {

			log.debug("Consuming record in Ewaste for notification: " + record.toString());
			ewasteRequest = mapper.convertValue(record, EwasteRegistrationRequest.class);
		} catch (final Exception e) {

			log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
		}

		log.info("Ewaste Appplication Received: " + ewasteRequest.getEwasteApplication().get(0).getRequestId());

		notificationService1.process(ewasteRequest);
	}

}
