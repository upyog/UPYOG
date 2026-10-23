package org.egov.asset.calculator.kafka.broker;

import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {

	private final CustomKafkaTemplate<String, Object> kafkaTemplate;

	public Producer(CustomKafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void push(String topic, Object value) {
		kafkaTemplate.send(topic, value);
	}
}
