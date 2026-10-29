package org.upyog.chb.kafka.consumer;


import java.util.HashMap;
import java.util.Map;

import org.egov.tracer.model.CustomException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.upyog.chb.service.PaymentNotificationService;

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

	private final PaymentNotificationService paymentNotificationService;

	public PaymentUpdateConsumer(PaymentNotificationService paymentNotificationService) {
		this.paymentNotificationService = paymentNotificationService;
	}

	/**
	 * Consumes payment receipt events and updates booking payment status.
	 *
	 * @param paymentRecord deserialized Kafka payload for the receipt event
	 * @param topic Kafka topic the message was received on
	 */
	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
	public void paymentSuccess(final Map<String, Object> paymentRecord,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("CHB Appplication Received to update status after payment success : " + topic);
		try {
			paymentNotificationService.process(paymentRecord, topic);
		} catch (JsonProcessingException e) {
			log.error("Exception occurred while processing payment reciept : ", e.getMessage());
		}

	}
	
	/**
	 * Consumes payment-gateway transaction update events.
	 *
	 * @param paymentRecord deserialized Kafka payload for the PG transaction update
	 * @param topic Kafka topic the message was received on
	 */
	@KafkaListener(topics = { "${kafka.topics.update.pg.txns}" })
	public void paymentUpdate(final Map<String, Object> paymentRecord,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("CHB Appplication payment status update for  : " + topic + " and record : " + paymentRecord);
		paymentNotificationService.processTransaction(paymentRecord, topic, null);

	}

	/**
	 * Returns the map of the values required from the transaction payload.
	 *
	 * @param documentContext The DocumentContext of the transaction object
	 * @return The required values as key,value pair
	 */
	@SuppressWarnings("unused")
	private Map<String, String> getValuesFromTransaction(DocumentContext documentContext) {
		HashMap<String, String> valMap = new HashMap<>();

		try {
			String txnStatus = documentContext.read("$.Transaction.txnStatus");
			valMap.put("txnStatus", txnStatus);

			String txnAmount = documentContext.read("$.Transaction.txnAmount");
			valMap.put("txnAmount", txnAmount);

			String tenantId = documentContext.read("$.Transaction.tenantId");
			valMap.put("tenantId", tenantId);

			String moduleId = documentContext.read("$.Transaction.consumerCode");
			valMap.put("moduleId", moduleId);
			valMap.put("bookingNo", moduleId);

			String mobileNumber = documentContext.read("$.Transaction.user.mobileNumber");
			valMap.put("mobileNumber", mobileNumber);
		} catch (Exception e) {
			log.error("Transaction Object Parsing: ", e);
			throw new CustomException("PARSING ERROR", "Failed to fetch values from the Transaction Object");
		}

		return valMap;
	}
}
