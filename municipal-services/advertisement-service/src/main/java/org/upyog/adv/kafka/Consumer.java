package org.upyog.adv.kafka;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Consumer {

    /*
    * Uncomment the below line to start consuming record from kafka.topics.consumer
    * Value of the variable kafka.topics.consumer should be overwritten in application.properties
    */
    //@KafkaListener(topics = {"kafka.topics.consumer"})
    public void listen(final Map<String, Object> messagePayload) {
        // Placeholder for future Kafka consumption
    }
}
