package org.upyog.reconciliation.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReconciliationProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Pushes a message to the specified Kafka topic.
     *
     * @param topic the Kafka topic to produce to
     * @param value the payload to send
     */
    public void push(String topic, Object value) {
        log.info("Pushing message to topic: {}", topic);
        kafkaTemplate.send(topic, value);
    }
}
