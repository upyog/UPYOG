package org.upyog.cdwm.kafka.consumer;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.upyog.cdwm.service.PaymentService;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;


/**
 * Kafka consumer class that listens to payment-related messages on the configured topic.
 * <p>
 * This consumer listens to the receipt creation topic and delegates the processing
 * of payment events to the {@link PaymentService}. It is responsible for initiating 
 * updates to the workflow status and application details upon successful payment.
 * </p>
 * 
 * <p>
 * Expected message format is a {@code HashMap<String, Object>} representing the 
 * serialized {@link org.upyog.cdwm.model.PaymentRequest}.
 * </p>
 *
 * @author Neha
 */

@Component
@Slf4j
public class PaymentUpdateConsumer {

	@Autowired
	private PaymentService paymentService;

	 /**
     * Listens to the Kafka topic for payment receipt creation events and triggers the
     * payment processing logic.
     *
     * @param record the received message from the Kafka topic, containing payment details.
     * @param topic the name of the topic from which the message was consumed.
     */
	
	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("CND Appplication Received to update workflow after PAY ");
		try {
			paymentService.process(record, topic);
		} catch (JsonProcessingException e) {
			log.error("JsonProcessingException occurred while processing payment record in CND consumer: {}", e.getMessage(), e);
			
		}

	}
}
