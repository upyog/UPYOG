package org.bel.birthdeath.crdeath.kafka.producer;

import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
     * Creates CrDeathProducer for kafka push 
     * Rakhi S IKM
     * 
     */

@Service
public class CrDeathProducer {
    @Autowired
    private CustomKafkaTemplate<String, Object> kafkaTemplate;

    public void push(String topic, Object value) {
        kafkaTemplate.send(topic, value);
    }
}
