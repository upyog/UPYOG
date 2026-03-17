package org.egov.noc.consumer;

import java.util.HashMap;

import org.egov.noc.service.notification.NOCNotificationService;
import org.egov.noc.util.NOCConstants;
import org.egov.noc.web.model.NocRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class NOCConsumer {

	@Autowired
	private NOCNotificationService notificationService;

	@KafkaListener(
			topics = {
					"${persister.save.noc.topic}",
					"${persister.update.noc.topic}",
					"${persister.update.noc.workflow.topic}"
			},
			concurrency = "${kafka.consumer.config.concurrency.count}",
			groupId = "${spring.kafka.consumer.group-id}"
	)
	public void listen(final String rawRecord) {
		ObjectMapper mapper = new ObjectMapper();
		NocRequest nocRequest = new NocRequest();
		
		// Register JavaTimeModule
        mapper.registerModule(new JavaTimeModule());

        // Optional: Serialize dates as ISO strings instead of timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		
		try {
			log.debug("Consuming record: " + rawRecord);
			nocRequest = mapper.readValue(rawRecord, NocRequest.class);
		} catch (final Exception e) {
			log.error("Error while listening to value: " + rawRecord + ": " + e);
		}
		log.debug("BPA Received: " + nocRequest.getNoc().getApplicationNo());
		if(!nocRequest.getNoc().getWorkflow().getAction().equalsIgnoreCase(NOCConstants.ACTION_PAY))
			notificationService.process(nocRequest, rawRecord);
	}
}
