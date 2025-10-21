package org.egov.ndc.consumer;

import java.util.List;

import org.egov.ndc.service.notification.NDCNotificationService;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class NDCConsumer {

	@Autowired
	private NDCNotificationService notificationService;
	
	@KafkaListener(topics = { "${persister.save.ndc.topic}", "${persister.update.ndc.topic}" })
	public void listen(final String record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		log.info("Incoming raw message: {}", record);
		ObjectMapper mapper = new ObjectMapper();
		NdcApplicationRequest ndcRequest = new NdcApplicationRequest();
		try {
			log.debug("Consuming record: " + record);
			ndcRequest = mapper.readValue(record, NdcApplicationRequest.class);
		} catch (final Exception e) {
			log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
		}
		List<Application> applications = ndcRequest.getApplications();
		log.debug("BPA Received: " + applications);
		notificationService.process(ndcRequest);
	}
}
