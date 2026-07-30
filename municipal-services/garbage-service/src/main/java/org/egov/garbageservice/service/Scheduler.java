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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scheduled service responsible for automated periodic garbage billing and demand generation.
 *
 * <p>This scheduler leverages a cron expression to routinely scan for approved and active
 * garbage accounts that are due for billing on the current date, and invokes the
 * {@link DemandService} to generate their billing demands.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final GarbageAccountRepository garbageAccountRepository;
    private final DemandService demandService;

    /**
     * Automatically triggered cron job that evaluates and processes all eligible garbage accounts for billing.
     *
     * <p>This method executes based on the configured {@code grbg.scheduler.cron} property. It
     * uses ShedLock to ensure only a single instance of this job runs across distributed environments.
     */

    @Scheduled(cron = "${grbg.scheduler.cron}")
    @SchedulerLock(name = "GarbageScheduler_generateDemand", lockAtLeastFor = "PT10M", lockAtMostFor = "PT30M")
    public void generateDemands() {
        LocalDate today = LocalDate.now();
        process(today, RequestInfo.builder().build());
    }

    /**
     * Allows for manual invocation of the billing generation process for a specific date.
     *
     * <p>If a billing date is not explicitly provided, it defaults to the current date.
     * This is useful for administrative backfilling or triggering ad-hoc billing cycles.
     *
     * @param requestInfo the contextual information for the API request
     * @param billingDate the specific date for which bills should be generated (optional)
     * @return a {@link String} message summarizing the number of demands generated
     */

    public String triggerManually(RequestInfo requestInfo, LocalDate billingDate) {
        LocalDate date = (billingDate != null ? billingDate : LocalDate.now());
        log.info("Manual scheduler trigger for {}", date);
        return process(date, requestInfo);
    }

    /**
     * Core processing logic that identifies due accounts and generates demands.
     *
     * <p>The process follows these steps:
     * <ol>
     *   <li>Queries the repository for all garbage accounts with an APPROVED status.</li>
     *   <li>Filters the accounts down to those that are due for billing on the given date using {@link #isBillingDue}.</li>
     *   <li>Iterates through the eligible accounts and delegates to {@link DemandService#generateDemand}.</li>
     *   <li>Accumulates the successful generation count and returns a summary message.</li>
     * </ol>
     *
     * @param billingDate the target date for evaluating billing eligibility
     * @param requestInfo the contextual request info block
     * @return a summary string indicating how many demands were successfully generated
     */

    private String process(LocalDate billingDate, RequestInfo requestInfo) {

        SearchCriteriaGarbageAccount criteria = SearchCriteriaGarbageAccount.builder()
                .status(Arrays.asList(ServiceConstants.STATUS_APPROVED, ServiceConstants.STATUS_PAID))
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

    /**
     * Determines whether a specific garbage account is due for billing on the given scheduler date.
     *
     * <p>The logic checks the account's original approval date. An account is due if the current
     * scheduler date is on the same day of the month as the original approval date. It also accounts
     * for edge cases where the approval day is beyond the end of the current month (e.g., Feb 28th).
     *
     * @param account       the {@link GarbageAccount} being evaluated
     * @param schedulerDate the date against which the account's billing cycle is checked
     * @return {@code true} if the account is due for billing; {@code false} otherwise
     */

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