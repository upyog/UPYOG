package org.egov.ptr.consumer;

import java.util.HashMap;

import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.service.PaymentNotificationService;
import org.egov.ptr.web.contracts.PetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReceiptConsumer {

	@Autowired
	private PaymentNotificationService paymentNotificationService;

	@Autowired
	private PetConfiguration config;
	@Autowired
	private ObjectMapper mapper;

	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
	public void listenPayments(final HashMap<String, Object> record,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

//		PetRequest petRequest = new PetRequest();
		
		PetRegistrationRequest petRequest= new PetRegistrationRequest();
		try {

			log.debug("Consuming record: " + record);
			petRequest = mapper.convertValue(record, PetRegistrationRequest.class);
		} catch (final Exception e) {

			log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
		}
		log.info("Pet Appplication Received to update workflow after PAY: "+petRequest.getPetRegistrationApplications().get(0).getApplicationNumber());
		paymentNotificationService.process(petRequest, topic);

	}
}
