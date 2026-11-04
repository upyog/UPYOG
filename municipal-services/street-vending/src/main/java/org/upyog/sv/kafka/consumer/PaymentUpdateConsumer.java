package org.upyog.sv.kafka.consumer;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.upyog.sv.service.PaymentService;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentUpdateConsumer {

	private final PaymentService paymentService;

	public PaymentUpdateConsumer(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
	public void listen(final Map<String, Object> messagePayload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("Street Vending Appplication Received to update workflow after PAY ");
		try {
			paymentService.process(messagePayload);
		} catch (JsonProcessingException e) {
			log.info("Catch block in listenPayments method of Pet service consumer");
			e.printStackTrace();
		}

	}

}
