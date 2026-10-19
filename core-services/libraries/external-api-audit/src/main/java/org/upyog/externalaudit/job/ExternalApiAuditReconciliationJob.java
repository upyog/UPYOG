package org.upyog.externalaudit.job;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.upyog.externalaudit.config.ExternalApiAuditProperties;
import org.upyog.externalaudit.constants.ExternalApiAuditConstants;
import org.upyog.externalaudit.repository.IntegrationAuditRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Marks INITIATED rows that never received a SUCCESS/FAILED event as TIMED_OUT.
 * Enable only on the service whose datasource hosts {@code ug_external_api_*}.
 * ShedLock keeps a single pod running this cron when that service is scaled.
 */
@Slf4j
@Component
@ConditionalOnBean(IntegrationAuditRepository.class)
@ConditionalOnProperty(prefix = "external.api.audit.reconciliation", name = "enabled", havingValue = "true")
public class ExternalApiAuditReconciliationJob {

    private final IntegrationAuditRepository integrationAuditRepository;
    private final ExternalApiAuditProperties properties;

    public ExternalApiAuditReconciliationJob(IntegrationAuditRepository integrationAuditRepository,
            ExternalApiAuditProperties properties) {
        this.integrationAuditRepository = integrationAuditRepository;
        this.properties = properties;
    }

    /**
     * Scheduled entry: INITIATED rows with {@code request_time} older than
     * {@link ExternalApiAuditProperties#getStaleThresholdMs()} become {@code TIMED_OUT}.
     */
    @Scheduled(cron = "${external.api.audit.reconciliation.cron:0 0 6 * * *}",
            zone = "${external.api.audit.reconciliation.zone:Asia/Kolkata}")
    @SchedulerLock(name = ExternalApiAuditConstants.RECONCILIATION_LOCK,
            lockAtLeastFor = "PT5M", lockAtMostFor = "PT30M")
    public void reconcileStaleRequests() {
        long now = System.currentTimeMillis();
        long requestTimeThreshold = now - properties.getStaleThresholdMs();
        int updatedRows = integrationAuditRepository.markTimedOutInitiatedRequests(requestTimeThreshold, now);
        log.info("Integration audit reconciliation job completed. Marked {} stale requests as TIMED_OUT.", updatedRows);
    }
}
