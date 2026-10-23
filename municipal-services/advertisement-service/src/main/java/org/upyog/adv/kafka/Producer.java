package org.upyog.adv.kafka;

import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.stereotype.Service;

// NOTE: If tracer is disabled change CustomKafkaTemplate to KafkaTemplate in autowiring

@Service("advProducer")
@Slf4j
public class Producer {

    private final CustomKafkaTemplate<String, Object> kafkaTemplate;

    public Producer(CustomKafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void push(String topic, Object value) {
        kafkaTemplate.send(topic, value);
    }
}
