package org.egov.ndc.consumer;

import java.util.List;

import org.egov.ndc.service.notification.NDCNotificationService;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class NDCConsumer {

	private final NDCNotificationService notificationService;

	public NDCConsumer(NDCNotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@KafkaListener(topics = { "${persister.save.ndc.topic}", "${persister.update.ndc.topic}" }, concurrency = "${kafka.consumer.config.concurrency.count}")
	public void listen(final String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		ObjectMapper mapper = new ObjectMapper();
		NdcApplicationRequest ndcRequest = new NdcApplicationRequest();
		try {
			ndcRequest = mapper.readValue(message, NdcApplicationRequest.class);
		} catch (final Exception e) {
			log.error("Error while listening to value: " + message + " on topic: " + topic + ": " + e);
		}
		List<Application> applications = ndcRequest.getApplications();
		log.debug("Received: " + applications);
		notificationService.process(ndcRequest);
	}
}
