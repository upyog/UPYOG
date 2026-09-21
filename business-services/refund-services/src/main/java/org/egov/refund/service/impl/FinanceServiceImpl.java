package org.egov.refund.service.impl;

import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.kafka.producer.Producer;
import org.egov.refund.service.FinanceService;
import org.egov.refund.web.contracat.RefundRequest;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FinanceServiceImpl implements FinanceService {

	private final Producer producer;

	private final String financeTopic;
	private final String financeCompletedTopic;
	

	public FinanceServiceImpl(Producer producer, ApplicationProperties applicationProperties) {

		this.producer = producer;
		this.financeTopic = applicationProperties.getFinanceTopic();
		this.financeCompletedTopic = applicationProperties.getEgovRefundFinancePaymentTopic();
	}

	@Override
	public void processRefund(RefundRequest request) {
		producer.push(financeTopic, request);
		log.info("Refund published to finance successfully. refundId={}, topic={}", request.getRefund().getConsumerCode(),financeTopic);
	}
	
	@Override
	public void sendToFinanceComplete(RefundRequest request) {
		producer.push(financeCompletedTopic, request);
		log.info("Refund published to finance successfully. refundId={}, topic={}", request.getRefund().getConsumerCode(),financeCompletedTopic);
	}
}