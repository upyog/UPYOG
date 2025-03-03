package org.upyog.rs.kafka.consumer;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.upyog.rs.service.RequestServiceNotificationService;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationConsumer {

	@Autowired
	private RequestServiceNotificationService notificationService;

	@Autowired
	private ObjectMapper mapper;

	@KafkaListener(topics = { "${persister.update.water-tanker.topic}", "${persister.create.water-tanker.topic}" })
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		WaterTankerBookingRequest waterTankerRequest = new WaterTankerBookingRequest();
		try {

			waterTankerRequest = mapper.convertValue(record, WaterTankerBookingRequest.class);
		} catch (final Exception e) {
			log.error("Error while processing RS notification to value: " + record + " on topic: " + topic + ": " + e);
		}

		notificationService.process(waterTankerRequest);
	}

}
