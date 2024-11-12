package org.upyog.sv.kafka.consumer;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.upyog.sv.service.StreetyVendingNotificationService;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.springframework.kafka.support.KafkaHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationConsumer {

	@Autowired
	private StreetyVendingNotificationService notificationService;

	@Autowired
	private ObjectMapper mapper;

//	@KafkaListener(topics = { "${persister.update.street-vending.topic}", "${persister.create.street-vending.topic}" })
//	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
//
//		StreetVendingRequest vendingRequest = new StreetVendingRequest();
//		try {
//
//			log.info("Consuming record in CHB for notification: " + record.toString() + " from topic: " + topic);
//			log.info("Strigifed json : " + StreetVendingUtil.beuatifyJson(record));
//			vendingRequest = mapper.convertValue(record, StreetVendingRequest.class);
//		} catch (final Exception e) {
//			log.error("Error while processing CHB notification to value: " + record + " on topic: " + topic + ": " + e);
//		}
//
//		String bookingStatus = vendingRequest.getStreetVendingDetail().getApplicationStatus();
//		log.info("CHB Appplication Received with application no : "
//				+ vendingRequest.getStreetVendingDetail().getApplicationNo() + " and for status : " +  bookingStatus);
//		
//		//Send notification to user except PENDING_FOR_PAYMENT status
//		/*
//		 * if (!S.PENDING_FOR_PAYMENT.toString().equals(bookingStatus)) {
//		 * CommunityHallBookingDetail bookingDetail =
//		 * bookingRequest.getHallsBookingApplication(); if (bookingDetail.getWorkflow()
//		 * == null || bookingDetail.getWorkflow().getAction() == null) { bookingStatus =
//		 * bookingDetail.getBookingStatus(); } else { bookingStatus =
//		 * bookingDetail.getWorkflow().getAction(); }
//		 * 
//		 * log.info(" booking status bookingDetail.getWorkflow() : " +
//		 * bookingDetail.getWorkflow());
//		 * 
//		 * notificationService.process(vendingRequest, bookingStatus); }
//		 */
//	}

}
