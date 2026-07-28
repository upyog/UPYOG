package org.egov.garbageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccount;
import org.egov.garbageservice.repository.GarbageAccountRepository;
import org.egov.garbageservice.util.ServiceConstants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final GarbageAccountRepository garbageAccountRepository;
    private final DemandService demandService;

    @Scheduled(cron = "${grbg.scheduler.cron}")
    @SchedulerLock(name = "GarbageScheduler_generateDemand", lockAtLeastFor = "PT10M", lockAtMostFor = "PT30M")
    public void generateDemands() {
        LocalDate today = LocalDate.now();
        process(today, RequestInfo.builder().build());
    }

    public String triggerManually(RequestInfo requestInfo, LocalDate billingDate) {
        LocalDate date = (billingDate != null ? billingDate : LocalDate.now());
        log.info("Manual scheduler trigger for {}", date);
        return process(date, requestInfo);
    }

    private String process(LocalDate billingDate, RequestInfo requestInfo) {

        SearchCriteriaGarbageAccount criteria = SearchCriteriaGarbageAccount.builder()
                .status(Collections.singletonList(ServiceConstants.STATUS_APPROVED))
                .build();

        List<GarbageAccount> garbageAccounts =
                garbageAccountRepository.searchV2(criteria);

        if (garbageAccounts.isEmpty()) {
            return "No approved garbage accounts found";
        }

        List<GarbageAccount> activeGarbageAccounts =
                garbageAccounts.stream()
                        .filter(a -> isBillingDue(a, billingDate))
                        .collect(Collectors.toList());

        if (activeGarbageAccounts.isEmpty()) {
            return "No active Garbage Accounts found";
        }

        int generated = 0;

        for (GarbageAccount account : activeGarbageAccounts) {
            try {

                demandService.generateDemand(
                        requestInfo,
                        account,
                        billingDate);

                generated++;

                log.info(
                        "Demand generated for {}",
                        account.getGrbgApplicationNumber());

            } catch (Exception e) {

                log.error(
                        "Failed for {}",
                        account.getGrbgApplicationNumber(),
                        e);
            }
        }
        String result = "Generated: " + generated;

        log.info("Scheduler done — {}", result);

        return result;
    }

    private boolean isBillingDue(GarbageAccount account, LocalDate schedulerDate) {

        Long approvalTimestamp = account.getApprovalDate();

        if (approvalTimestamp == null) {
            return false;
        }

        LocalDate approvalDate = Instant.ofEpochMilli(approvalTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (schedulerDate.isBefore(approvalDate)) {
            return false;
        }

        int billingDay = Math.min(
                approvalDate.getDayOfMonth(),
                schedulerDate.lengthOfMonth());

        return schedulerDate.getDayOfMonth() == billingDay;
    }
}