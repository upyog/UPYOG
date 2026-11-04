package org.upyog.dx.upyogdata.pfms.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.upyog.dx.upyogdata.pfms.service.PFMSForwardingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.dx.upyogdata.constants.Constants;

/**
 * Scheduler that periodically picks up INITIATED transactions
 * and forwards them to the PFMS external API.
 *
 * Cron is configurable via pfms.scheduler.cron in application.properties.
 * Default: every 5 minutes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PFMSForwardingScheduler {

    private final PFMSForwardingService pfmsForwardingService;

    @Scheduled(cron = "${pfms.scheduler.cron}")
    public void run() {
        log.info("Pfms Daily Scheduler Triggered");
        pfmsForwardingService.forwardInitiatedTransactions(Constants.SCHEDULER);
    }
}
