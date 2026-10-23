package org.egov.ewst.producer;

import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for producing messages to Kafka topics.
 * This class uses a custom Kafka template to send messages to the specified topic.
 */
@Service
public class Producer {

	private final CustomKafkaTemplate<String, Object> kafkaTemplate;

	public Producer(CustomKafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	/**
	 * Pushes a message to the specified Kafka topic.
	 *
	 * @param topic The name of the Kafka topic to which the message will be sent.
	 * @param value The message payload to be sent to the topic.
	 */
	public void push(String topic, Object value) {
		kafkaTemplate.send(topic, value);
	}
}
