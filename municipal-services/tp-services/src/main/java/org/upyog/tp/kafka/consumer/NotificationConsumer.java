package org.upyog.tp.kafka.consumer;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.upyog.tp.service.NotificationService;
import org.upyog.tp.web.models.Workflow;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Consumer service for processing Kafka messages related to tree pruning bookings.
 * It listens to specific Kafka topics, extracts relevant booking details, and triggers notifications.
 */
@Service
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper mapper;

    public NotificationConsumer(NotificationService notificationService, ObjectMapper mapper) {
        this.notificationService = notificationService;
        this.mapper = mapper;
    }


    /**
     * Listens to Kafka topics for updates and creation events of tree pruning bookings.
     *
     * @param kafkaRecord the incoming Kafka message containing booking details
     * @param topic       the name of the Kafka topic from which the message was received
     */
    @KafkaListener(topics = {
            "${persister.create.tree-pruning.topic}",
            "${persister.create.tree-pruning.with.profile.topic}",
            "${persister.update.tree-pruning.topic}"
    })
    public void listen(final Map<String, Object> kafkaRecord, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        String applicationStatus;
        String bookingNo;

        try {
            if (!topic.contains("tree-pruning")) {
                log.error("Unknown topic: " + topic);
                return;
            }
            TreePruningBookingRequest treePruningRequest = mapper.convertValue(kafkaRecord, TreePruningBookingRequest.class);
            applicationStatus = treePruningRequest.getTreePruningBookingDetail().getBookingStatus();
            bookingNo = treePruningRequest.getTreePruningBookingDetail().getBookingNo();
            log.info("Tree Pruning Application Received with booking no: " + bookingNo + " and status: " + applicationStatus);
            applicationStatus = extractApplicationStatus(treePruningRequest.getTreePruningBookingDetail().getBookingStatus(),
                    treePruningRequest.getTreePruningBookingDetail().getWorkflow());
            log.info("Final Application Status: " + applicationStatus);
            notificationService.process(treePruningRequest, applicationStatus);
        } catch (final Exception e) {
            log.error("Error processing tree pruning notification: " + kafkaRecord + " on topic: " + topic, e);
        }
    }


    private String extractApplicationStatus(String bookingStatus, Workflow workflow) {
        if (workflow == null) {
            return bookingStatus;
        }
        try {
            String action = workflow.getAction();
            return action != null ? action : bookingStatus;
        } catch (Exception e) {
            log.error("Error extracting workflow action: ", e);
            return bookingStatus;
        }
    }
}
