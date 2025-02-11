package org.egov.individual.producer;

import org.egov.common.producer.Producer;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.stereotype.Component;

@Component("individualProducer")
public class IndividualProducer extends Producer {
    public IndividualProducer(CustomKafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }
}
