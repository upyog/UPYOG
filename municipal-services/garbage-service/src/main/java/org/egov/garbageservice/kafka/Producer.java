package org.egov.garbageservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Generic Kafka producer wrapper for pushing messaging payloads to configured topics.
 */
@Component
@RequiredArgsConstructor
public class Producer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void push(String topic, Object value) {
        kafkaTemplate.send(topic, value);
    }
}