package org.upyog.chb.kafka.producer;

import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

// NOTE: If tracer is disabled change CustomKafkaTemplate to KafkaTemplate in autowiring

@Service("chbProducer")
@Slf4j
public class Producer {

    @Autowired
    private CustomKafkaTemplate<String, Object> kafkaTemplate;

    public void push(String topic, Object value) {
    	try {
    		kafkaTemplate.send(topic, value);
		} catch (Exception e) {
			log.error("Exception occured while sending message to topic : " + topic);
			log.error("Exception details : " + e);
		}
    }
}
