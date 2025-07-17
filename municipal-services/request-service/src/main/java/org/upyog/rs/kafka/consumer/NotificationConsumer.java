package org.upyog.rs.kafka.consumer;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.upyog.rs.service.RequestServiceNotificationService;
import org.upyog.rs.web.models.Workflow;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingRequest;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
* Consumer service for processing Kafka messages related to water tanker and mobile toilet bookings.
* It listens to specific Kafka topics, extracts relevant booking details, and triggers notifications.
*/
@Service
@Slf4j
public class NotificationConsumer {

    @Autowired
    private RequestServiceNotificationService notificationService;

    @Autowired
    private ObjectMapper mapper;


    /**
     * Listens to Kafka topics for updates and creation events of water tanker and mobile toilet bookings.
     *
     * @param record the incoming Kafka message containing booking details
     * @param topic  the name of the Kafka topic from which the message was received
     */
    
    @KafkaListener(topics = { 
            "${persister.update.water-tanker.topic}", "${persister.create.water-tanker.topic}", "${persister.create.water-tanker.with.profile.topic}",
            "${persister.update.mobile-toilet.topic}", "${persister.create.mobile-toilet.topic}" , "${persister.create.mobile-toilet.with.profile.topic}"
    })
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        Object request = null;
        String applicationStatus = null;
        String bookingNo = null;

        try {
            if (topic.contains("water-tanker")) {
                WaterTankerBookingRequest waterTankerRequest = mapper.convertValue(record, WaterTankerBookingRequest.class);
                request = waterTankerRequest;
                applicationStatus = waterTankerRequest.getWaterTankerBookingDetail().getBookingStatus();
                bookingNo = waterTankerRequest.getWaterTankerBookingDetail().getBookingNo();
            } else if (topic.contains("mobile-toilet")) {
                MobileToiletBookingRequest mobileToiletRequest = mapper.convertValue(record, MobileToiletBookingRequest.class);
                request = mobileToiletRequest;
                applicationStatus = mobileToiletRequest.getMobileToiletBookingDetail().getBookingStatus();
                bookingNo = mobileToiletRequest.getMobileToiletBookingDetail().getBookingNo();
            } else {
                log.error("Unknown topic: " + topic);
                return;
            }
        } catch (final Exception e) {
            log.error("Error processing RS notification: " + record + " on topic: " + topic, e);
            return;
        }

        log.info("Request Service Application Received with booking no: " + bookingNo + " and status: " + applicationStatus);

        if (request instanceof WaterTankerBookingRequest) {
            WaterTankerBookingRequest waterTankerRequest = (WaterTankerBookingRequest) request;
            applicationStatus = extractApplicationStatus(waterTankerRequest.getWaterTankerBookingDetail().getBookingStatus(),
                    waterTankerRequest.getWaterTankerBookingDetail().getWorkflow());
        } else if (request instanceof MobileToiletBookingRequest) {
            MobileToiletBookingRequest mobileToiletRequest = (MobileToiletBookingRequest) request;
            applicationStatus = extractApplicationStatus(mobileToiletRequest.getMobileToiletBookingDetail().getBookingStatus(),
                    mobileToiletRequest.getMobileToiletBookingDetail().getWorkflow());
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

