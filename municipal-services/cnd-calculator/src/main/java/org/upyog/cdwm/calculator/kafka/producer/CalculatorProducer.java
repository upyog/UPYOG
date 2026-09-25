package org.upyog.cdwm.calculator.kafka.producer;

import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CalculatorProducer {

	private final CustomKafkaTemplate<String, Object> kafkaTemplate;

	public CalculatorProducer(CustomKafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	/**
	 * Listener method to push records to kafka queue.
	 * @param topic The kafka topic to push to
	 * @param value The object to be pushed
	 */
	public void push(String topic, Object value) {
		kafkaTemplate.send(topic, value);
	}

}
