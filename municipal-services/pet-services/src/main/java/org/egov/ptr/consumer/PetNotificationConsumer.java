package org.egov.ptr.consumer;

import java.util.HashMap;

import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.service.PTRNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.KafkaHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetNotificationConsumer {

	@Autowired
	private PTRNotificationService notificationService;

	@Autowired
	private ObjectMapper mapper;

	@KafkaListener(topics = { "${ptr.kafka.create.topic}", "${ptr.kafka.update.topic}" })
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		PetRegistrationRequest petRequest = new PetRegistrationRequest();
		try {

			log.debug("Consuming record: " + record);
			petRequest = mapper.convertValue(record, PetRegistrationRequest.class);
		} catch (final Exception e) {

			log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
		}

		log.info("Pet Appplication Received: "
				+ petRequest.getPetRegistrationApplications().get(0).getApplicationNumber());

		notificationService.process(petRequest);
	}

}
