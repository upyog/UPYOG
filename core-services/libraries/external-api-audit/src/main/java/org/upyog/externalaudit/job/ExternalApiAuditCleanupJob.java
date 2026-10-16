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
 * Deletes audit rows older than the configured retention window.
 * Enable only on the service whose datasource hosts {@code ug_external_api_*}.
 * ShedLock keeps a single pod running this cron when that service is scaled.
 */
@Slf4j
@Component
@ConditionalOnBean(IntegrationAuditRepository.class)
@ConditionalOnProperty(prefix = "external.api.audit.cleanup", name = "enabled", havingValue = "true")
public class ExternalApiAuditCleanupJob {

    private final IntegrationAuditRepository integrationAuditRepository;
    private final ExternalApiAuditProperties properties;

    public ExternalApiAuditCleanupJob(IntegrationAuditRepository integrationAuditRepository,
            ExternalApiAuditProperties properties) {
        this.integrationAuditRepository = integrationAuditRepository;
        this.properties = properties;
    }

    /**
     * Scheduled entry: deletes audit rows whose {@code created_time} is older than the retention window.
     */
    @Scheduled(cron = "${external.api.audit.cleanup.cron:0 30 3 * * *}",
            zone = "${external.api.audit.cleanup.zone:Asia/Kolkata}")
    @SchedulerLock(name = ExternalApiAuditConstants.CLEANUP_LOCK,
            lockAtLeastFor = "PT5M", lockAtMostFor = "PT30M")
    public void deleteExpiredRecords() {
        long createdTimeThreshold = System.currentTimeMillis() - properties.getCleanup().getRetentionMs();
        int deleted = integrationAuditRepository.deleteExpiredAuditRecords(createdTimeThreshold);
        log.info("Integration audit cleanup job completed. Deleted {} rows.", deleted);
    }
}
