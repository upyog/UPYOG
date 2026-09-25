package org.upyog.cdwm.kafka;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Consumer {

    /*
    * Uncomment the below line to start consuming record from kafka.topics.consumer
    * Value of the variable kafka.topics.consumer should be overwritten in application.properties
    */
    //@KafkaListener(topics = {"kafka.topics.consumer"})
    public void listen(final Map<String, Object> consumerRecord) {
        // No-op: enable @KafkaListener and implement processing when consumer topic is configured
    }
}
