package org.upyog.pgrai.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.upyog.pgrai.service.NotificationService;
import org.upyog.pgrai.util.PGRConstants;
import org.upyog.pgrai.web.models.ServiceRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Consumer class for handling notification-related Kafka messages.
 * Processes notification requests by consuming messages from the configured Kafka topic.
 */
@Component
@Slf4j
public class NotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper mapper;

    /**
     * Listens to the configured Kafka topic for notification requests.
     * Converts the received record into a `ServiceRequest` object and processes it.
     *
     * @param record The Kafka message payload as a map.
     * @param topic  The name of the Kafka topic from which the message was received.
     */
    @KafkaListener(topicPattern = "${pgr.kafka.notification.topic.pattern}")
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            ServiceRequest request = mapper.convertValue(record, ServiceRequest.class);

            String tenantId = request.getService().getTenantId();

            // Adding tenant ID to MDC for tracing purposes
            MDC.put(PGRConstants.TENANTID_MDC_STRING, tenantId);

            notificationService.process(request, topic);
        } catch (Exception ex) {
            StringBuilder builder = new StringBuilder("Error while listening to value: ").append(record)
                    .append(" on topic: ").append(topic);
            log.error(builder.toString(), ex);
        }
    }
}