package org.bel.birthdeath.crdeath.kafka.consumer;

import java.util.HashMap;

import org.bel.birthdeath.crdeath.kafka.producer.CrDeathProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;

@Slf4j
@Component
public class Consumer {
    
    @Autowired
	private CrDeathProducer producer;

    @KafkaListener(topics = {"${persister.save.crdeath.topic}"})
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
        
        } catch (final Exception e) {
            log.error("Error while listening to value: " + record + " on topic: " + topic + ": ", e.getMessage());
        }
    }

}
