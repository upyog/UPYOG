package org.upyog.rs.kafka.consumer;

import java.util.HashMap;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.upyog.rs.service.PaymentService;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentUpdateConsumer {

	private final PaymentService paymentService;

	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
	public void listen(final HashMap<String, Object> consumerRecord) {

		log.info("Water Tanker Appplication Received to update workflow after PAY ");
		try {
			paymentService.process(consumerRecord);
		} catch (JsonProcessingException e) {
			log.info("Catch block in listenPayments method of Request service consumer");
			e.printStackTrace();
		}

	}
}
