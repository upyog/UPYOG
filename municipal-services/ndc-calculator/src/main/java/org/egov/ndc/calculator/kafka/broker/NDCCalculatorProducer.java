package org.egov.ndc.calculator.kafka.broker;

import java.util.UUID;

import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NDCCalculatorProducer {

	private final CustomKafkaTemplate<String, Object> kafkaTemplate;

	public NDCCalculatorProducer(CustomKafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	/**
	 * Listener method to push records to kafka queue.
	 * @param topic The kafka topic to push to
	 * @param value The object to be pushed
	 */
	public void push(String topic, Object value) {
		String key = UUID.randomUUID().toString();
		kafkaTemplate.send(topic, key, value);
	}

	/**
	 * Listener method to push records to kafka queue with a custom key.
	 * @param topic The kafka topic to push to
	 * @param key The key for the message
	 * @param value The object to be pushed
	 */
	public void push(String topic, String key, Object value) {
		kafkaTemplate.send(topic, key, value);
	}

}
