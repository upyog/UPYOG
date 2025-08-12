package org.upyog.sv.kafka.consumer;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.upyog.sv.service.StreetyVendingNotificationService;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer service responsible for listening to street vending events
 * and processing notifications accordingly.
 */

@Service
@Slf4j
public class NotificationConsumer {

	@Autowired
	private StreetyVendingNotificationService notificationService;

	@Autowired
	private ObjectMapper mapper;

    /**
     * Listens to Kafka topics related to street vending updates and processes
     * notifications accordingly.
     *
     * @param record the message payload received from Kafka
     * @param topic  the name of the Kafka topic from which the message was received
     */
	@KafkaListener(topics = { "${persister.update.street-vending.topic}", "${persister.create.street-vending.topic}"})
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		StreetVendingRequest vendingRequest = new StreetVendingRequest();
		try {

			vendingRequest = mapper.convertValue(record, StreetVendingRequest.class);
		} catch (final Exception e) {
			log.error("Error while processing SV notification to value: " + record + " on topic: " + topic + ": " + e);
		}

		String applicationStatus = vendingRequest.getStreetVendingDetail().getApplicationStatus();
		log.info("CND Appplication Received with booking no : "
				+ vendingRequest.getStreetVendingDetail().getApplicationNo() + " and for status : " +  applicationStatus);
		
		//Send notification to user except PENDING_FOR_PAYMENT status
		if (!applicationStatus.equals("PENDING_FOR_PAYMENT")) {
			StreetVendingDetail applicationDetail = vendingRequest.getStreetVendingDetail();
			if (applicationDetail.getWorkflow() == null || applicationDetail.getWorkflow().getAction() == null) {
				applicationStatus = applicationDetail.getApplicationStatus();
			} else {
				applicationStatus = applicationDetail.getWorkflow().getAction();
			}

			log.info(" Application status applicationDetail.getWorkflow() : " + applicationDetail.getWorkflow());

			notificationService.process(vendingRequest, applicationStatus);
		}

	}

}
