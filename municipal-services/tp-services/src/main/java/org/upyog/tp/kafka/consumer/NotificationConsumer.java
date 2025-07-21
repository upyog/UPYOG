package org.upyog.tp.kafka.consumer;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper mapper;


    /**
     * Listens to Kafka topics for updates and creation events of tree pruning bookings.
     *
     * @param record the incoming Kafka message containing booking details
     * @param topic  the name of the Kafka topic from which the message was received
     */

    @KafkaListener(topics = {
            "${persister.update.tree-pruning.topic}", "${persister.create.tree-pruning.topic}, ${persister.create.tree-pruning.with.profile.topic}"
    })
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        Object request = null;
        String applicationStatus = null;
        String bookingNo = null;

        try {
            if (topic.contains("tree-pruning")) {
                TreePruningBookingRequest treePruningRequest = mapper.convertValue(record, TreePruningBookingRequest.class);
                request = treePruningRequest;
                applicationStatus = treePruningRequest.getTreePruningBookingDetail().getBookingStatus();
                bookingNo = treePruningRequest.getTreePruningBookingDetail().getBookingNo();
            } else {
                log.error("Unknown topic: " + topic);
                return;
            }
        } catch (final Exception e) {
            log.error("Error processing tree pruning notification: " + record + " on topic: " + topic, e);
            return;
        }

        log.info("Tree Pruning Application Received with booking no: " + bookingNo + " and status: " + applicationStatus);

        if (request instanceof TreePruningBookingRequest) {
            TreePruningBookingRequest treePruningRequest = (TreePruningBookingRequest) request;
            applicationStatus = extractApplicationStatus(treePruningRequest.getTreePruningBookingDetail().getBookingStatus(),
                    treePruningRequest.getTreePruningBookingDetail().getWorkflow());
        }

        log.info("Final Application Status: " + applicationStatus);
        notificationService.process(request, applicationStatus);
    }


    private String extractApplicationStatus(String bookingStatus, Workflow workflow) {
        if (workflow == null) return bookingStatus;
        try {
            String action = workflow.getAction();
            return action != null ? action.toString() : bookingStatus;
        } catch (Exception e) {
            log.error("Error extracting workflow action: ", e);
            return bookingStatus;
        }
    }
}

