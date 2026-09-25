package org.upyog.cdwm.kafka.consumer;

import java.util.HashMap;
import java.util.Map;

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
 * Expected message format is a {@code Map<String, Object>} representing the 
 * serialized {@link org.upyog.cdwm.model.PaymentRequest}.
 * </p>
 *
 * @author Neha
 */

@Component
@Slf4j
public class PaymentUpdateConsumer {

	private final PaymentService paymentService;

	public PaymentUpdateConsumer(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	 /**
     * Listens to the Kafka topic for payment receipt creation events and triggers the
     * payment processing logic.
     *
     * @param consumerRecord the received message from the Kafka topic, containing payment details.
     * @param topic the name of the topic from which the message was consumed.
     */
	
	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
	public void listen(final Map<String, Object> consumerRecord, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("CND Appplication Received to update workflow after PAY ");
		try {
			paymentService.process(new HashMap<>(consumerRecord), topic);
		} catch (JsonProcessingException e) {
			log.error("JsonProcessingException occurred while processing payment record in CND consumer: {}", e.getMessage(), e);
			
		}

	}
}
