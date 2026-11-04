package org.upyog.dx.upyogdata.pfms.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.upyog.dx.upyogdata.pfms.service.PFMSForwardingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.dx.upyogdata.constants.Constants;

@Slf4j
@Component
@RequiredArgsConstructor
public class PFMSRetryScheduler {

    private final PFMSForwardingService pfmsForwardingService;

    @Scheduled(cron = "${pfms.retry.scheduler.cron}")
    public void run() {
        log.info("Pfms Retry Scheduler Triggered");
        pfmsForwardingService.retryFailedTransactions(Constants.SCHEDULER);
    }
}
