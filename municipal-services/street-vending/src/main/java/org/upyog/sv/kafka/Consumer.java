package org.upyog.sv.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.upyog.sv.service.PaymentNotificationService;

import java.util.HashMap;

@Slf4j
@Component
public class Consumer {

	@Autowired
	private PaymentNotificationService paymentNotificationService;

//	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
//	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
//
//		log.info("Street Vending Appplication Received to update workflow after PAY ");
//		try {
//			paymentNotificationService.process(record, topic);
//		} catch (JsonProcessingException e) {
//			log.info("Catch block in listenPayments method of Pet service consumer");
//			e.printStackTrace();
//		}
//
//	}
}
