package org.egov.refund.service.impl;

import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.service.FinanceService;
import org.egov.refund.web.contracat.RefundRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FinanceServiceImpl implements FinanceService {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	private final String financeTopic;

	public FinanceServiceImpl(KafkaTemplate<String, Object> kafkaTemplate,
			ApplicationProperties applicationProperties) {

		this.kafkaTemplate = kafkaTemplate;
		this.financeTopic = applicationProperties.getFinanceTopic();
	}

	@Override
	public void processRefund(RefundRequest request) {

		

		kafkaTemplate.send(financeTopic, request.getRefund().getRefundNo(), request);
	}
}