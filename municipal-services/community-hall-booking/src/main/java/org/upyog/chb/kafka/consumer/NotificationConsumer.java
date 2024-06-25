package org.upyog.chb.kafka.consumer;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.upyog.chb.service.CHBNotificationService;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.springframework.kafka.support.KafkaHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationConsumer {

	@Autowired
	private CHBNotificationService notificationService;

	@Autowired
	private ObjectMapper mapper;

	@KafkaListener(topics = { "${persister.save.communityhall.booking.topic}", "${persister.update.communityhall.booking.topic}" })
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		CommunityHallBookingRequest bookingRequest = new CommunityHallBookingRequest();
		try {

			log.debug("Consuming record in CHB for notification: " + record.toString());
			bookingRequest = mapper.convertValue(record, CommunityHallBookingRequest.class);
		} catch (final Exception e) {
			log.error("Error while processing CHB notification to value: " + record + " on topic: " + topic + ": " + e);
		}

		log.info("CHB Appplication Received: "
				+ bookingRequest.getHallsBookingApplication().getBookingNo());

		//TODO :  Remove this once integrated
		//notificationService.process(bookingRequest);
	}

}
