package org.upyog.chb.kafka.consumer;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.service.CHBNotificationService;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.springframework.kafka.support.KafkaHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * This class acts as a Kafka consumer for handling notifications related to
 * Community Hall Booking events.
 * 
 * Purpose:
 * - To listen to Kafka topics for booking-related events such as creation and updates.
 * - To process the consumed records and trigger appropriate notification services.
 * 
 * Dependencies:
 * - CHBNotificationService: Used to send notifications based on the booking events.
 * - ObjectMapper: Used to map the consumed Kafka record into a CommunityHallBookingRequest object.
 * 
 * Kafka Listener:
 * - Listens to the topics specified in the application properties:
 *   1. ${persister.save.communityhall.booking.topic}: Topic for booking creation events.
 *   2. ${persister.update.communityhall.booking.topic}: Topic for booking update events.
 * 
 * Features:
 * - Logs the consumed record and topic for debugging and monitoring purposes.
 * - Converts the consumed record into a CommunityHallBookingRequest object.
 * - Handles exceptions gracefully by logging errors during record processing.
 * 
 * Usage:
 * - This class is automatically managed by Spring as a Kafka consumer.
 * - It processes booking-related events and triggers notifications accordingly.
 */

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

			log.info("Consuming record in CHB for notification: " + record.toString() + " from topic: " + topic);
			//log.info("Strigifed json : " + CommunityHallBookingUtil.beuatifyJson(record));
			bookingRequest = mapper.convertValue(record, CommunityHallBookingRequest.class);
		} catch (final Exception e) {
			log.error("Error while processing CHB notification to value: " + record + " on topic: " + topic + ": " + e);
		}

		String bookingStatus = bookingRequest.getHallsBookingApplication().getBookingStatus();
		log.info("CHB Appplication Received with booking no : "
				+ bookingRequest.getHallsBookingApplication().getBookingNo() + " and for status : " +  bookingStatus);
		
		//Send notification to user except PENDING_FOR_PAYMENT status
		if (!BookingStatusEnum.PENDING_FOR_PAYMENT.toString().equals(bookingStatus)) {
			CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
			if (bookingDetail.getWorkflow() == null || bookingDetail.getWorkflow().getAction() == null) {
				bookingStatus = bookingDetail.getBookingStatus();
			} else {
				bookingStatus = bookingDetail.getWorkflow().getAction();
			}

			log.info(" booking status bookingDetail.getWorkflow() : " + bookingDetail.getWorkflow());

			notificationService.process(bookingRequest, bookingStatus);
		}
	}

}
