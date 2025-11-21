package org.upyog.rs.kafka.consumer;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.upyog.rs.service.PaymentService;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentUpdateConsumer {

	@Autowired
	private PaymentService paymentService;

	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("Water Tanker Appplication Received to update workflow after PAY ");
		try {
			paymentService.process(record, topic);
		} catch (JsonProcessingException e) {
			log.info("Catch block in listenPayments method of Request service consumer");
			e.printStackTrace();
		}

	}
}
