package org.upyog.chb.kafka.consumer;


import java.util.HashMap;
import java.util.Map;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.upyog.chb.service.PaymentNotificationService;
import org.upyog.chb.util.CommunityHallBookingUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.DocumentContext;

import lombok.extern.slf4j.Slf4j;

/**
 * This class acts as a Kafka consumer for handling payment update events
 * related to the Community Hall Booking module.
 * 
 * Purpose:
 * - To listen to Kafka topics for payment-related events such as receipt creation.
 * - To process the consumed records and trigger appropriate actions for payment updates.
 * 
 * Dependencies:
 * - PaymentNotificationService: Used to handle notifications or updates based on payment events.
 * - CommunityHallBookingUtil: Utility class for common operations related to community hall booking.
 * 
 * Kafka Listener:
 * - Listens to the topic specified in the application properties:
 *   1. ${kafka.topics.receipt.create}: Topic for payment receipt creation events.
 * 
 * Features:
 * - Logs the consumed record and topic for debugging and monitoring purposes.
 * - Processes the payment success events and updates the booking status accordingly.
 * - Handles exceptions gracefully by logging errors during record processing.
 * 
 * Usage:
 * - This class is automatically managed by Spring as a Kafka consumer.
 * - It processes payment-related events and triggers updates or notifications as needed.
 */

@Component
@Slf4j
public class PaymentUpdateConsumer {

	@Autowired
	private PaymentNotificationService paymentNotificationService;
	

	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
	public void paymentSuccess(final HashMap<String, Object> record,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("CHB Appplication Received to update status after payment success : " + topic);
		//TODO: need to remove after testing
		log.info("Strigifed json : " + CommunityHallBookingUtil.beuatifyJson(record));
		try {
			paymentNotificationService.process(record, topic);
		} catch (JsonProcessingException e) {
			log.error("Exception occurred while processing payment reciept : ", e.getMessage());
		}

	}
	
	@KafkaListener(topics = { "${kafka.topics.update.pg.txns}" })
	public void paymentUpdate(final HashMap<String, Object> record,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("CHB Appplication payment status update for  : " + topic + " and record : " + record);
		//TODO: need to remove after testing
		log.info("Strigifed json : " + CommunityHallBookingUtil.beuatifyJson(record));
		paymentNotificationService.processTransaction(record, topic, null);

	}
	
	/**
	 * Handling this use case with timer table so its not required now 
	 * 
	 * @param record
	 * @param topic
	 */
	
	//@KafkaListener(topics = { "${kafka.topics.save.pg.txns}" })
	/*
	 * public void paymentStarted(final HashMap<String, Object> record,
	 * 
	 * @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
	 * 
	 * log.info("CHB Appplication payment started for topic  : " + topic +
	 * " and record : " + record); //TODO: need to remove after testing
	 * log.info("Strigifed json : " +
	 * CommunityHallBookingUtil.beuatifyJson(record));
	 * paymentNotificationService.processTransaction(record, topic,
	 * BookingStatusEnum.PENDING_FOR_PAYMENT);
	 * 
	 * }
	 */
	
	
	/**
	 * Returns the map of the values required from the record
	 * 
	 * @param documentContext
	 *            The DocumentContext of the record Object
	 * @return The required values as key,value pair
	 */
	@SuppressWarnings("unused")
	private Map<String, String> getValuesFromTransaction(DocumentContext documentContext) {
		String txnStatus, txnAmount, moduleId, tenantId, mobileNumber, module;
		HashMap<String, String> valMap = new HashMap<>();

		try {
			txnStatus = documentContext.read("$.Transaction.txnStatus");
			valMap.put("txnStatus", txnStatus);

			txnAmount = documentContext.read("$.Transaction.txnAmount");
			valMap.put("txnAmount", txnAmount.toString());

			tenantId = documentContext.read("$.Transaction.tenantId");
			valMap.put("tenantId", tenantId);

			moduleId = documentContext.read("$.Transaction.consumerCode");
			valMap.put("moduleId", moduleId);
			valMap.put("bookingNo", moduleId);
			// valMap.put("assessmentNumber",moduleId.split(":")[1]);

			mobileNumber = documentContext.read("$.Transaction.user.mobileNumber");
			valMap.put("mobileNumber", mobileNumber);

			// module =
			// documentContext.read("$.Transaction.taxAndPayments[0].businessService");
			// valMap.put("module",module);
		} catch (Exception e) {
			log.error("Transaction Object Parsing: ", e);
			throw new CustomException("PARSING ERROR", "Failed to fetch values from the Transaction Object");
		}

		return valMap;
	}
}
