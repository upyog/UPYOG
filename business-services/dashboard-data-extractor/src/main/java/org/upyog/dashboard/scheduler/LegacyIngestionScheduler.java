package org.upyog.dashboard.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.service.LegacyIngestionService;
import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring-managed scheduler for automated bulk historical (legacy) data backfilling.
 * Manages two separate scheduled workflows:
 * 1. Populating the legacy queue with pending daily jobs.
 * 2. Executing pending/failed legacy ingestion jobs.
 */
@Slf4j
@Component
public class LegacyIngestionScheduler {

    @Autowired
    private LegacyIngestionService legacyIngestionService;

    @Autowired
    private DashboardProperties dashboardProperties;

    private boolean legacyIngestionEnabled;
    private int defaultMonths;

    @PostConstruct
    public void init() {
        this.legacyIngestionEnabled = dashboardProperties.isLegacyIngestionEnabled();
        this.defaultMonths = dashboardProperties.getLegacyDefaultMonths();
    }

    /**
     * Scheduler 1: Checks for missing legacy dates/jobs and populates them in the table.
     */
    @Scheduled(cron = "${legacy.ingestion.populate.cron}")
    public void populateLegacyJobs() {
        if (!legacyIngestionEnabled) {
            log.debug("LegacyIngestionScheduler | Legacy job populator is disabled via legacy.ingestion.enabled=false");
            return;
        }

        log.info("LegacyIngestionScheduler | Populating legacy jobs for past {} months...", defaultMonths);
        int createdCount = legacyIngestionService.populateLegacyJobs(defaultMonths, null);
        log.info("LegacyIngestionScheduler | Created {} new legacy jobs in the queue.", createdCount);
    }

    /**
     * Scheduler 2: Fetches pending or failed jobs and executes them.
     */
    @Scheduled(cron = "${legacy.ingestion.execute.cron}")
    public void executeLegacyJobs() {
        if (!legacyIngestionEnabled) {
            log.debug("LegacyIngestionScheduler | Legacy job executor is disabled via legacy.ingestion.enabled=false");
            return;
        }

        log.info("LegacyIngestionScheduler | Processing pending legacy jobs...");
        int limit = 50; // Batch limit per execution loop
        int executedCount = legacyIngestionService.executeLegacyJobs(limit, null);
        log.info("LegacyIngestionScheduler | Executed {} pending legacy jobs.", executedCount);
    }
}
