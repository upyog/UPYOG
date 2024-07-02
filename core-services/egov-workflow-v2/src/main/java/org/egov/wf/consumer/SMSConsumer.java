package org.egov.wf.consumer;

import java.util.HashMap;

import org.egov.wf.service.SMSNotificationService;
import org.egov.wf.web.models.ProcessInstanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SMSConsumer {

	@Autowired
	private SMSNotificationService notificationService;
	
	@KafkaListener(topics = { "${kafka.topics.notification.sms}"})
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		ObjectMapper mapper = new ObjectMapper();
		ProcessInstanceRequest processInstanceRequest = new ProcessInstanceRequest();
		try {
			log.debug("Consuming record: " + record);
			processInstanceRequest = mapper.convertValue(record, ProcessInstanceRequest.class);
		} catch (final Exception e) {
			log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
		}
		//log.debug("BPA Received: " + processInstanceRequest.getBPA().getApplicationNo());
		notificationService.process(processInstanceRequest,topic);
	}
}
