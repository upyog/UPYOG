package org.egov.echallan.producer;

import java.util.UUID;

import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {

	private final CustomKafkaTemplate<String, Object> kafkaTemplate;

	public Producer(CustomKafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void push(String topic, Object value) {
		addedKeyPush(topic, value);
	}

	public void addedKeyPush(String topic, Object value) {
		String key = UUID.randomUUID().toString();
		kafkaTemplate.send(topic, key, value);
	}
}
