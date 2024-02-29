package org.egov.ptr.consumer;

import java.util.HashMap;

import org.egov.ptr.service.PaymentNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReceiptConsumer {

	@Autowired
	private PaymentNotificationService paymentNotificationService;

	@KafkaListener(topics = { "${kafka.topics.receipt.create}" })
	public void listenPayments(final HashMap<String, Object> record,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		log.info("Pet Appplication Received to update workflow after PAY ");
		paymentNotificationService.process(record, topic);

	}
}
