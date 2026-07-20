package org.upyog.adapter.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.adapter.service.LegacyIngestionService;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring-managed scheduler for automated bulk historical (legacy) data backfilling.
 *
 * <p>Controlled by properties:
 * <ul>
 *   <li>{@code legacy.ingestion.enabled} — toggle (default: {@code false})</li>
 *   <li>{@code legacy.ingestion.cron} — cron schedule (default: 2 AM on the 1st of every month)</li>
 *   <li>{@code legacy.ingestion.default-months} — lookback months (default: 5)</li>
 * </ul>
 */
@Slf4j
@Component
public class LegacyIngestionScheduler {

    @Autowired
    private LegacyIngestionService legacyIngestionService;

    @Value("${legacy.ingestion.enabled:false}")
    private boolean legacyIngestionEnabled;

    @Value("${legacy.ingestion.default-months:5}")
    private int defaultMonths;

    /**
     * Executes automated bulk historical backfilling if enabled by configuration.
     */
    @Scheduled(cron = "${legacy.ingestion.cron:0 0 2 1 * ?}")
    public void executeLegacyIngestion() {
        if (!legacyIngestionEnabled) {
            log.debug("LegacyIngestionScheduler | Legacy ingestion scheduler is disabled via legacy.ingestion.enabled=false");
            return;
        }

        log.info("LegacyIngestionScheduler | Triggering historical ingestion for past {} months...", defaultMonths);
        legacyIngestionService.ingestHistoricalDataForLastMonths(defaultMonths, null);
    }
}
