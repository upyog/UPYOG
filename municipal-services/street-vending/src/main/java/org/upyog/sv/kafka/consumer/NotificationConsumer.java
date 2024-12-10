package org.upyog.sv.kafka.consumer;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.upyog.sv.service.StreetyVendingNotificationService;
import org.upyog.sv.util.NotificationUtil;
import org.upyog.sv.web.models.StreetVendingRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationConsumer {

	@Autowired
	private StreetyVendingNotificationService notificationService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NotificationUtil util;

	@KafkaListener(topics = { "${persister.update.street-vending.topic}", "${persister.create.street-vending.topic}" })
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		StreetVendingRequest vendingRequest = new StreetVendingRequest();
		try {

			vendingRequest = mapper.convertValue(record, StreetVendingRequest.class);
		} catch (final Exception e) {
			log.error("Error while processing SV notification to value: " + record + " on topic: " + topic + ": " + e);
		}

		notificationService.process(vendingRequest);
	}

}
