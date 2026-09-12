package org.egov.refund.kafka.producer;

import org.egov.refund.web.contracat.RefundRequest;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

// NOTE: If tracer is disabled change CustomKafkaTemplate to KafkaTemplate in autowiring

@Component
@Slf4j
public class Producer {

    private final CustomKafkaTemplate<String, Object> kafkaTemplate;

	Producer(CustomKafkaTemplate kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

    public void push(String topic, Object value) {
    	try {
    		kafkaTemplate.send(topic, value);
		} catch (Exception e) {
			log.error("Exception occured while sending message to topic : " + topic);
			log.error("Exception details : " + e);
		}
    }

	public void push(String financeTopic, String refundNo, RefundRequest request) {
		// TODO Auto-generated method stub
		
	}
}
