package org.egov.bpa.producer;

import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PreApprovedProducer {

	@Autowired
	private CustomKafkaTemplate<String, Object> kafkaTemplate;

	/**
	 * Push the data to kafka in the provided topic
	 * 
	 * @param topic
	 * @param value
	 */
	public void push(String topic, Object value) {
		log.info("kafka send topic - "+topic);
		kafkaTemplate.send(topic, value);
	}
}
