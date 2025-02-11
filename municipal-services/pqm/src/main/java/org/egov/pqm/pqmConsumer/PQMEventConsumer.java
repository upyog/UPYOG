package org.egov.pqm.pqmConsumer;

import java.util.HashMap;

import org.egov.pqm.util.PQMEventUtil;
import org.egov.pqm.web.model.TestRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PQMEventConsumer {
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PQMEventUtil eventProcessingUtil;

	@KafkaListener(topics = { "${egov.test.create.kafka.topic}", "${egov.test.update.kafka.topic}" })
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		try {
			log.info("Received event on topic - " + record);
			TestRequest pqmEvent = mapper.convertValue(record, TestRequest.class);
			log.info("Received event on topic - " + topic + "Received Request body for processPQMEvent " + pqmEvent);
			eventProcessingUtil.processPQMEvent(pqmEvent);
		} catch (Exception e) {
			throw new CustomException("PARSING_ERROR", "Failed to parse record to pqmEvent" + e.getMessage());
		}
	}

}
