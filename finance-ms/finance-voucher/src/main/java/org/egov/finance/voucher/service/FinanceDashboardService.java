package org.egov.finance.voucher.service;

import org.egov.finance.voucher.enumeration.FinanceEventType;
import org.egov.finance.voucher.model.FinanceDashboardEvent;
import org.egov.finance.voucher.util.ApplicationThreadLocals;
import org.egov.finance.voucher.util.MicroserviceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class FinanceDashboardService {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private MicroserviceUtils microServiceUtil;

	@Value("${finance.esk.dashboard.event.enabled}")
	private boolean isEsDashboardIndexingEnabled;

	public void publishEvent(FinanceEventType eventType, Object data) {
		if (isEsDashboardIndexingEnabled) {
			String tenantId = microServiceUtil.getTenentId();
			String token = microServiceUtil.generateAdminToken(tenantId);
			String domainName = ApplicationThreadLocals.getDomainName();
			FinanceDashboardEvent event = new FinanceDashboardEvent(this, data, eventType, tenantId, token, domainName);
			eventPublisher.publishEvent(event);
			log.info("EVENT : {} PUBLISHED TO ESK DASHBOARD , ", event);
		} else {
			log.info("ESK Dashboard is not enable for indexing to ES");
		}
	}
}
