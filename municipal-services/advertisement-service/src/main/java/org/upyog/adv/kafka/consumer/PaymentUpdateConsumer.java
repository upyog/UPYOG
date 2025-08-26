package org.upyog.adv.kafka.consumer;


import java.util.HashMap;
import java.util.Map;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.service.PaymentService;
import org.upyog.adv.util.BookingUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.DocumentContext;

import lombok.extern.slf4j.Slf4j;
/**
 * This class acts as a Kafka consumer for payment updates in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Listens to Kafka topics for payment receipt creation events.
 * - Processes payment success events and updates the booking workflow accordingly.
 * - Logs and handles exceptions during message processing.
 * 
 * Dependencies:
 * - PaymentService: Handles the business logic for processing payment updates.
 * - BookingUtil: Provides utility methods for JSON beautification and other operations.
 * 
 * Annotations:
 * - @Component: Marks this class as a Spring-managed component.
 * - @Slf4j: Enables logging for debugging and monitoring.
 * - @KafkaListener: Configures the method to listen to specific Kafka topics.
 */
@Component
@Slf4j
public class PaymentUpdateConsumer {

	@Autowired
	private PaymentService paymentService;
	

	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
	public void paymentSuccess(final HashMap<String, Object> record,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("ADV Appplication Received to update workflow after PAY for topic : " + topic);
		//TODO: need to remove after testing
		log.info("Strigifed json : " + BookingUtil.beuatifyJson(record));
		try {
			paymentService.process(record, topic);
		} catch (JsonProcessingException e) {
			log.error("Exception occurred while processing payment reciept : ", e.getMessage());
		}

	}
	
	@KafkaListener(topics = { "${kafka.topics.update.pg.txns}" })
	public void paymentUpdate(final HashMap<String, Object> record,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("ADV Appplication payment status update for  : " + topic + " and record : " + record);
		//TODO: need to remove after testing
		log.info("Strigifed json : " + BookingUtil.beuatifyJson(record));
		paymentService.processTransaction(record, topic, null);

	}
	
	
	@KafkaListener(topics = { "${kafka.topics.save.pg.txns}" })
	public void paymentStarted(final HashMap<String, Object> record,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("ADV Appplication payment started for topic  : " + topic + " and record : " + record);
		//TODO: need to remove after testing
		log.info("Strigifed json : " + BookingUtil.beuatifyJson(record));
		paymentService.processTransaction(record, topic, BookingStatusEnum.PENDING_FOR_PAYMENT);

	}
	
	
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
