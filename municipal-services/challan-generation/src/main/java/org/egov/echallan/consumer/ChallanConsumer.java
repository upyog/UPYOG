package org.egov.echallan.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.model.ChallanRequest;
import org.egov.echallan.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.util.Map;



@Slf4j
@Component
public class ChallanConsumer {

    private final NotificationService notificationService;

    private final ChallanConfiguration config;
    
    @Autowired
    public ChallanConsumer(NotificationService notificationService, ChallanConfiguration config) {
        this.notificationService = notificationService;
        this.config = config;
    }

    @KafkaListener(topics = {"${persister.save.echallan.topic}","${persister.update.echallan.topic}"},concurrency = "${kafka.consumer.config.concurrency.count}")
    public void listen(final Map<String, Object> messagePayload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
        ObjectMapper mapper = new ObjectMapper();
        // Configure to ignore unknown properties like localityCode getter
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ChallanRequest challanRequest = mapper.convertValue(messagePayload, ChallanRequest.class);

        // Skip notifications if disabled (e.g., when localization messages are not configured)
        if (config.getIsNotificationEnabled() != null && !config.getIsNotificationEnabled()) {
        	log.info("Notifications are disabled. Skipping notification for challan: {}", 
        		challanRequest.getChallan() != null ? challanRequest.getChallan().getChallanNo() : "unknown");
        	return;
        }
        
        if(topic.equalsIgnoreCase(config.getSaveChallanTopic()))
        	notificationService.sendChallanNotification(challanRequest,true);
        else if(topic.equalsIgnoreCase(config.getUpdateChallanTopic()))
            notificationService.sendChallanNotification(challanRequest,false);
        } catch (final Exception e) {
        	e.printStackTrace();
            log.error("Error while listening to value: " + messagePayload + " on topic: " + topic + ": " + e);
        }
    }
}
