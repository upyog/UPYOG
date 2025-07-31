package org.upyog.cdwm.kafka.consumer;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.notification.CNDNotificationService;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Consumer service for processing Kafka messages related to CND application updates.
 * 
 * This class listens to Kafka topics and processes incoming messages to trigger 
 * notifications based on application status changes.
 */

@Service
@Slf4j
public class NotificationConsumer {

	@Autowired
	private CNDNotificationService notificationService;

	@Autowired
	private ObjectMapper mapper;

	 /**
     * Listens to Kafka topics for CND application events and processes notifications.
     *
     * @param record The Kafka message payload containing application details.
     * @param topic  The name of the Kafka topic from which the message was received.
     * 
     * This method:
     * - Converts the received message into a {@link CNDApplicationRequest}.
     * - Logs the received application details.
     * - Checks the application status and triggers notifications unless the status is "PENDING_FOR_PAYMENT".
     * - Extracts the appropriate action from the workflow and processes the notification.
     */
	
	@KafkaListener(topics = { "${persister.update.cnd.service.topic}", "${persister.create.cnd.service.topic}","${persister.create.cnd.service.with.profile.topic}" })
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		CNDApplicationRequest cndRequest = new CNDApplicationRequest();
		try {

			cndRequest = mapper.convertValue(record, CNDApplicationRequest.class);
		} catch (final Exception e) {
			log.error("Error while processing cnd notification to value: " + record + " on topic: " + topic + ": " + e);
		}
		
		String applicationStatus = cndRequest.getCndApplication().getApplicationStatus();
		log.info("CND Appplication Received with booking no : "
				+ cndRequest.getCndApplication().getApplicationNumber() + " and for status : " +  applicationStatus);
		
		//Send notification to user except PENDING_FOR_PAYMENT status
		if (!applicationStatus.equals("PENDING_FOR_PAYMENT")) {
			CNDApplicationDetail applicationDetail = cndRequest.getCndApplication();
			if (applicationDetail.getWorkflow() == null || applicationDetail.getWorkflow().getAction() == null) {
				applicationStatus = applicationDetail.getApplicationStatus();
			} else {
				applicationStatus = applicationDetail.getWorkflow().getAction();
			}

			log.info(" Application status applicationDetail.getWorkflow() : " + applicationDetail.getWorkflow().getAction());

			notificationService.process(cndRequest, applicationStatus);
		}

	}

}
