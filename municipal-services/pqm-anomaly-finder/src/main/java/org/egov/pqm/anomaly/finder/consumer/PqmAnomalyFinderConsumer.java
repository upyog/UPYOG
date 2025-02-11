package org.egov.pqm.anomaly.finder.consumer;

import java.util.HashMap;

import org.egov.pqm.anomaly.finder.service.AnomalyFinderService;
import org.egov.pqm.anomaly.finder.web.model.TestRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PqmAnomalyFinderConsumer {
	
	
	@Autowired
	private AnomalyFinderService anomalyFinderService;
	
	@KafkaListener(topics = { "${persister.save.pqm.topic}", "${egov.pqm.anomaly.testNotSubmitted.kafka.topic}"})
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		ObjectMapper mapper = new ObjectMapper();
		TestRequest testRequest = new TestRequest();
		try {
			log.debug("Consuming record: " + record);
			testRequest = mapper.convertValue(record, TestRequest.class);
		} catch (final Exception e) {
			log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
		}
		log.debug("FSM Received: " + testRequest.getTests().get(0).getTestId());
		anomalyFinderService.anomalyCreate(testRequest,topic);
	}

}
