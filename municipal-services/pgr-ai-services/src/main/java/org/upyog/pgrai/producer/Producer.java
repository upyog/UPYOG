package org.upyog.pgrai.producer;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Producer class for sending messages to Kafka topics.
 * Handles state-specific topic resolution and message publishing.
 */
@Service
@Slf4j
public class Producer {

    @Autowired
    private CustomKafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private MultiStateInstanceUtil centralInstanceUtil;

    /**
     * Publishes a message to a Kafka topic.
     * Resolves the state-specific topic name based on the tenant ID.
     *
     * @param tenantId The tenant ID used to determine the state-specific topic.
     * @param topic    The base Kafka topic name.
     * @param value    The message payload to be sent.
     */
    public void push(String tenantId, String topic, Object value) {
        String updatedTopic = centralInstanceUtil.getStateSpecificTopicName(tenantId, topic);
        log.info("The Kafka topic for the tenantId : " + tenantId + " is : " + updatedTopic);
        kafkaTemplate.send(updatedTopic, value);
    }
}