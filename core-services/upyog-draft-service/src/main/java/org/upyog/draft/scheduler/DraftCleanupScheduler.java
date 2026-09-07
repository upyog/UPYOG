package org.upyog.draft.scheduler;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.upyog.draft.config.DraftConfiguration;
import org.upyog.draft.service.DraftService;
import org.upyog.draft.util.DraftConstants;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class DraftCleanupScheduler {

    private final DraftService draftService;
    private final DraftConfiguration configuration;

    @Value("${scheduler.draft.cleanup.enabled:true}")
    private boolean schedulerEnabled;

    public DraftCleanupScheduler(DraftService draftService, DraftConfiguration configuration) {
        this.draftService = draftService;
        this.configuration = configuration;
    }

    @Scheduled(cron = "${scheduler.draft.cleanup.cron:0 0 2 * * *}")
    @SchedulerLock(name = "draftCleanupJob", lockAtLeastFor = "PT5M", lockAtMostFor = "PT30M")
    public void runCleanup() {
        if (!schedulerEnabled) {
            log.debug("Draft cleanup scheduler is disabled");
            return;
        }

        log.info("Starting draft cleanup job");

        long activeCutoff = Instant.now().minus(configuration.getActiveTtlDays(), ChronoUnit.DAYS).toEpochMilli();
        int expiredActive = draftService.purgeActiveOlderThan(activeCutoff);
        log.info("Marked {} ACTIVE drafts older than {} days as DISCARDED",
                expiredActive, configuration.getActiveTtlDays());

        long submittedCutoff = Instant.now()
                .minus(configuration.getSubmittedRetentionDays(), ChronoUnit.DAYS).toEpochMilli();
        int deletedSubmitted = draftService.purgeByStatusOlderThan(
                DraftConstants.STATUS_SUBMITTED, submittedCutoff);
        log.info("Purged {} SUBMITTED drafts older than {} days",
                deletedSubmitted, configuration.getSubmittedRetentionDays());

        long discardedCutoff = Instant.now()
                .minus(configuration.getDiscardedRetentionDays(), ChronoUnit.DAYS).toEpochMilli();
        int deletedDiscarded = draftService.purgeByStatusOlderThan(
                DraftConstants.STATUS_DISCARDED, discardedCutoff);
        log.info("Purged {} DISCARDED drafts older than {} days",
                deletedDiscarded, configuration.getDiscardedRetentionDays());

        if (configuration.isOrphanReconciliationEnabled()) {
            int reconciled = draftService.reconcileOrphanedDrafts();
            log.info("Reconciled {} orphaned ACTIVE drafts with module_entity_id", reconciled);
        }

        log.info("Draft cleanup job completed");
    }
}
