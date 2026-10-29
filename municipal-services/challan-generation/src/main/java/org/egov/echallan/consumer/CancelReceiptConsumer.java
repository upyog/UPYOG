package org.egov.echallan.consumer;

import java.util.Map;

import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.repository.ChallanRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CancelReceiptConsumer {

	private final ChallanRepository challanRepository;

	private final ChallanConfiguration config;

	@KafkaListener(topics = {"${kafka.topics.receipt.cancel.name}"},concurrency = "${kafka.consumer.config.concurrency.count}")
    public void listen(final Map<String, Object> messagePayload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		try {
			if (config.getReceiptCancelTopic().equalsIgnoreCase(topic)) {
				log.info("received cancel receipt request--");
				challanRepository.updateChallanOnCancelReceipt(messagePayload);
			}
		} catch (final Exception e) {
			log.error("Error while listening to value: " + messagePayload + " on topic: " + topic + ": ", e.getMessage());
		}
    }
}
