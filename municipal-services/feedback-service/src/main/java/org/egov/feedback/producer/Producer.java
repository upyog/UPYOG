package org.egov.feedback.producer;



import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Producer {

    @Autowired
    private CustomKafkaTemplate<String, Object> kafkaTemplate;

    public void push(String topic, Object value) {
    	log.info("Value: " + value.toString());
		log.info("Topic: "+topic);
        kafkaTemplate.send(topic, value);
    }
}
