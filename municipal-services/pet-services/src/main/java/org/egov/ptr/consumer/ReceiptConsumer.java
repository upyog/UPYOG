package org.egov.ptr.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.ptr.models.collection.PaymentRequest;
import org.egov.ptr.service.PaymentNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReceiptConsumer {

	@Autowired
	private PaymentNotificationService paymentNotificationService;

	@org.springframework.beans.factory.annotation.Value("${kafka.topics.receipt.create}")
	private String receiptTopic;

	@javax.annotation.PostConstruct
	public void init() {
		log.info("ReceiptConsumer initialized - Listening to topic: {}, group: egov-pet-service", receiptTopic);
	}

	@KafkaListener(topics = {"${kafka.topics.receipt.create}"}, groupId = "${spring.kafka.consumer.group-id}")
	public void listenPayments(final String rawRecord, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		
		if (rawRecord == null || rawRecord.isEmpty()) {
			log.error("Received empty payment message from topic: {}", topic);
			return;
		}
		
		try {
			PaymentRequest record = new ObjectMapper().readValue(rawRecord, PaymentRequest.class);
			
			if (record == null || record.getPayment() == null || 
					record.getPayment().getPaymentDetails() == null || record.getPayment().getPaymentDetails().isEmpty()) {
				log.error("Invalid payment request structure");
				return;
			}
			
			String businessService = record.getPayment().getPaymentDetails().get(0).getBusinessService();
			String consumerCode = record.getPayment().getPaymentDetails().get(0).getBill() != null 
					? record.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode() : "N/A";
			
			log.info("Processing payment notification - businessService: {}, consumerCode: {}", businessService, consumerCode);
			
			paymentNotificationService.process(record, topic);
			
		} catch (JsonProcessingException e) {
			log.error("JSON deserialization error in payment notification: {}", e.getMessage(), e);
		} catch (Exception e) {
			log.error("Error processing payment notification: {}", e.getMessage(), e);
		}
	}
}
