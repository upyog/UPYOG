package org.egov.refund.kafka.consumer;

import java.util.HashMap;

import org.egov.refund.model.PaymentRefund;
import org.egov.refund.service.RefundService;
import org.egov.refund.web.contracat.PaymentRefundRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentRefundConsumer {

	private final ObjectMapper objectMapper;
	private final RefundService refundService;

	public PaymentRefundConsumer(ObjectMapper objectMapper, RefundService refundService) {

		this.objectMapper = objectMapper;
		this.refundService = refundService;
	}

	@KafkaListener(topics = "${refund.kafka.payment-refund-topic}")
	public void consume(final HashMap<String, Object> message) {

		log.info("Payment refund response received from Kafka. message={}", message);

		try {

			PaymentRefundRequest paymentRefundRequest = objectMapper.convertValue(message, PaymentRefundRequest.class);
			PaymentRefund paymentRefund = paymentRefundRequest.getRefund();
			validate(paymentRefund);

			log.info("Processing payment refund response. " + "refundId={}, status={}",
					paymentRefund.getRefundId(), paymentRefund.getStatus());

			refundService.processPaymentRefund(paymentRefund);

		} catch (Exception e) {

			log.error("Error while consuming payment refund response. message={}", message, e);

			throw new RuntimeException("Failed to process payment refund response", e);
		}
	}

	private void validate(PaymentRefund paymentRefund) {

		if (paymentRefund == null) {
			throw new IllegalArgumentException("Payment refund cannot be null");
		}

		if (paymentRefund.getRefundId() == null || paymentRefund.getRefundId().isBlank()) {
			throw new IllegalArgumentException("Payment refund ID cannot be null or empty");
		}

		if (paymentRefund.getStatus() == null || paymentRefund.getStatus().isBlank()) {
			throw new IllegalArgumentException("Payment refund status cannot be null or empty");
		}
	}
}
