package org.upyog.dashboard.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.service.LegacyIngestionService;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring-managed scheduler for automated bulk historical (legacy) data backfilling.
 * Manages two separate scheduled workflows:
 * 1. Populating the legacy queue with pending daily jobs.
 * 2. Executing pending/failed legacy ingestion jobs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LegacyIngestionScheduler {

    private final LegacyIngestionService legacyIngestionService;
    private final DashboardProperties dashboardProperties;

    private boolean legacyIngestionEnabled;
    private int defaultMonths;

    /**
     * Initialises service-level configuration values from {@link org.upyog.dashboard.config.DashboardProperties}
     * after bean construction. Sets the flags that control whether legacy ingestion is enabled
     * and the default look-back period in months.
     */
    @PostConstruct
    public void init() {
        this.legacyIngestionEnabled = dashboardProperties.isLegacyIngestionEnabled();
        this.defaultMonths = dashboardProperties.getLegacyDefaultMonths();
    }

    /**
     * Scheduled method (cron: {@code legacy.ingestion.populate.cron}) that scans the past
     * {@code defaultMonths} months and inserts missing date rows into the legacy job queue
     * for all enabled modules.
     *
     * <p>Does nothing when legacy ingestion is disabled via
     * {@code legacy.ingestion.enabled=false}.
     */
    @Scheduled(cron = "${legacy.ingestion.populate.cron}")
    public void populateLegacyJobs() {
        if (!legacyIngestionEnabled) {
            log.debug("Legacy job populator is disabled via legacy.ingestion.enabled=false");
            return;
        }

        log.info("Populating legacy jobs for past {} months...", defaultMonths);
        int createdCount = legacyIngestionService.populateLegacyJobs(defaultMonths, null);
        log.info("Created {} new legacy jobs in the queue.", createdCount);
    }

    /**
     * Scheduled method (cron: {@code legacy.ingestion.execute.cron}) that fetches up to 50
     * pending or failed legacy jobs from the queue and attempts to ingest them.
     *
     * <p>Does nothing when legacy ingestion is disabled via
     * {@code legacy.ingestion.enabled=false}.
     */
    @Scheduled(cron = "${legacy.ingestion.execute.cron}")
    public void executeLegacyJobs() {
        if (!legacyIngestionEnabled) {
            log.debug("Legacy job executor is disabled via legacy.ingestion.enabled=false");
            return;
        }

        log.info("Processing pending legacy jobs...");
        int limit = 50; // Batch limit per execution loop
        int executedCount = legacyIngestionService.executeLegacyJobs(limit, null);
        log.info("Executed {} pending legacy jobs.", executedCount);
    }
}
