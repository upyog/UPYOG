package org.egov.nationaldashboardingest.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.repository.IntegrationAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExternalApiAuditReconciliationJob {

    @Autowired
    private IntegrationAuditRepository integrationAuditRepository;

    @Autowired
    private ApplicationProperties applicationProperties;

    //@Scheduled(cron = "0 0 6 * * *", zone = "Asia/Kolkata")
    public void reconcileStaleRequests() {
        long now = System.currentTimeMillis();
        long requestTimeThreshold = now - applicationProperties.getIntegrationAuditStaleThresholdMs();
        int updatedRows = integrationAuditRepository.markTimedOutInitiatedRequests(requestTimeThreshold, now);
        log.info("Integration audit reconciliation job completed. Marked {} stale requests as TIMED_OUT.", updatedRows);
    }
}
