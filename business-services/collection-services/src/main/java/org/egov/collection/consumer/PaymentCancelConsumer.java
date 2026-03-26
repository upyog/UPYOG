package org.egov.collection.consumer;

import java.util.HashMap;

import org.egov.collection.model.PaymentRequest;
import org.egov.collection.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentCancelConsumer {

	private final PaymentService paymentService;
	private final ObjectMapper mapper;

	public PaymentCancelConsumer(PaymentService paymentService, ObjectMapper mapper) {
		this.paymentService = paymentService;
		this.mapper = mapper;
	}

	@KafkaListener(topics = ("${kafka.topic.cancel.refund.txn}"))
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		try {
			PaymentRequest paymentrRequest = mapper.convertValue(record, PaymentRequest.class);
			paymentService.cancelPayment(paymentrRequest);
		} catch (Exception ex) {
			StringBuilder builder = new StringBuilder("Error while listening to value: ").append(record)
					.append("on topic: ").append(topic);
			log.error(builder.toString(), ex);
		}
	}

}
