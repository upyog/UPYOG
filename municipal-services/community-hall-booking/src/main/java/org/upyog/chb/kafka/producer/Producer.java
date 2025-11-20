package org.upyog.chb.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private ObjectMapper objectMapper;

    public void push(String topic, Object value) {
    	try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);

            log.info("Producing message to topic [{}]:\n{}", topic, json);
    		kafkaTemplate.send(topic, value);

		} catch (Exception e) {
			log.error("Exception occured while sending message to topic : " + topic);
			log.error("Exception details : " + e);
		}
    }
}
