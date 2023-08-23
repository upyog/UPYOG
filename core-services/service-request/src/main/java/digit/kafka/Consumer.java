package digit.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.service.NotificationService;
import digit.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


import java.util.HashMap;

@Component
@Slf4j
public class Consumer {

    /*
    * Uncomment the below line to start consuming record from kafka.topics.consumer
    * Value of the variable kafka.topics.consumer should be overwritten in application.properties
    */
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = {"save-service-definition"})
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {
            ServiceDefinitionRequest request = mapper.convertValue(record, ServiceDefinitionRequest.class);
            //log.info(request.toString());
            notificationService.prepareEventAndSend(request);

        } catch (final Exception e) {

            log.error("Error while listening to value: " + record + " on topic: " + topic + ": ", e);
        }

    }
}
