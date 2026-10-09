package org.egov.nationaldashboardingest.producer;

import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Generic Kafka message producer component for publishing events across dashboard ingest topics.
 * <p>
 * Leverages {@link CustomKafkaTemplate} from eGov tracer library to ensure distributed tracing
 * context and headers are propagated transparently across message payloads.
 * </p>
 */
@Service
@Slf4j
public class Producer {

    @Autowired
    private CustomKafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a message payload to a specified Kafka topic without a message key.
     *
     * @param topic destination Kafka topic name
     * @param value payload object to be serialized and sent to Kafka
     */
    public void push(String topic, Object value) {
        kafkaTemplate.send(topic, value);
    }

    /**
     * Publishes a message payload to a specified Kafka topic with a partition routing key.
     * <p>
     * Used for keyed distribution (e.g. keying by S3 file path or tenant code) to ensure ordered
     * processing across Kafka partition consumers.
     * </p>
     *
     * @param topic destination Kafka topic name
     * @param key   partition message routing key
     * @param value payload object to be serialized and sent to Kafka
     */
    public void push(String topic, String key, Object value) {
        kafkaTemplate.send(topic, key, value);
    }
}
