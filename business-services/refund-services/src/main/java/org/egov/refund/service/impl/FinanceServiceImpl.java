package org.egov.refund.service.impl;

import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.kafka.producer.Producer;
import org.egov.refund.service.FinanceService;
import org.egov.refund.web.contracat.RefundRequest;
import org.springframework.stereotype.Service;

@Service
public class FinanceServiceImpl implements FinanceService {

	private final Producer producer;

	private final String financeTopic;

	public FinanceServiceImpl(Producer producer, ApplicationProperties applicationProperties) {

		this.producer = producer;
		this.financeTopic = applicationProperties.getFinanceTopic();
	}

	@Override
	public void processRefund(RefundRequest request) {
		producer.push(financeTopic, request);
	}
}