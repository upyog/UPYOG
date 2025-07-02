package org.upyog.employee.dasboard.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

@Service
@Slf4j
public class Producer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void push(String topic, Object value) {
        kafkaTemplate.send(topic, value);
    }
}
