package org.egov.ptr.consumer;

import java.util.HashMap;

import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.service.PTRNotificationService;
import org.egov.ptr.service.EnrichmentService;
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
	private EnrichmentService enrichmentService;

	@Autowired
	private ObjectMapper mapper;

	@KafkaListener(topics = { "${ptr.kafka.create.topic}", "${ptr.kafka.update.topic}" }, concurrency = "${kafka.consumer.config.concurrency.count}")
	public void listen(String jsonString, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
	    try {
	        log.info("Received message on topic: " + topic);
	        // Manually convert the String to the Object
	        PetRegistrationRequest petRequest = mapper.readValue(jsonString, PetRegistrationRequest.class);
	        
//	     /   enrichmentService.saveOwnerMetadata(petRequest);
	        notificationService.process(petRequest);
	    } catch (Exception e) {
	        log.error("Failed to process message", e);
	    }
	}

}
