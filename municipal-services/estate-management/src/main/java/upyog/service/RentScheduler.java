package upyog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import upyog.repository.DemandRepository;
import upyog.repository.EstateRepository;
import upyog.util.MdmsUtil;
import upyog.web.models.Allotment;
import upyog.web.models.AllotmentSearchCriteria;
import upyog.web.models.BillingCycle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentScheduler {

    private final EstateRepository estateRepository;
    private final DemandService    demandService;
    private final DemandRepository demandRepository;
    private final upyog.config.EstateConfiguration estateConfiguration;
    private final MdmsUtil mdmsUtil;

    /**
     * Triggers rent demand generation for all eligible allotments.
     * Runs based on the configured scheduler cron expression.
     */

    @Scheduled(cron = "${est.scheduler.cron}")
    @SchedulerLock(name = "RentScheduler_generateDemand", lockAtLeastFor = "PT10M", lockAtMostFor = "PT30M")
    public void generateDemands() {
        LocalDate today = LocalDate.now();
        process(today, RequestInfo.builder().build());
    }

    /**
     * Manually triggers demand generation for a specific billing date.
     *
     * @param requestInfo request information
     * @param billingDate billing date to process; current date is used when null
     * @return processing summary
     */
    public String triggerManually(RequestInfo requestInfo, LocalDate billingDate) {
        LocalDate date = (billingDate != null ? billingDate : LocalDate.now());
        log.info("Manual scheduler trigger for {}", date);
        return process(date, requestInfo);
    }

    /**
     * Processes eligible allotments and generates rent demands based on
     * configured billing cycles and agreement validity.
     *
     * @param billingDate billing execution date
     * @param requestInfo request information
     * @return processing summary containing generated and skipped counts
     */
    private String process(LocalDate billingDate, RequestInfo requestInfo) {

        List<Allotment> allotments =
                estateRepository.searchAllotments(
                        new AllotmentSearchCriteria());

        if (allotments.isEmpty()) {
            return "No allotments found";
        }

        List<Allotment> activeAllotments =
                allotments.stream()
                        .filter(a -> isActive(a, billingDate))
                        .filter(a -> isBillingDue(a, billingDate))
                        .collect(Collectors.toList());

        if (activeAllotments.isEmpty()) {
            return "No active allotments found";
        }

        BigDecimal penaltyRate = mdmsUtil.getPenaltyRate(requestInfo, activeAllotments.get(0).getTenantId());
        log.info(
                "Penalty rate from MDMS: {}%",
                penaltyRate.multiply(BigDecimal.valueOf(100)));

        int generated = 0;
        int skipped = 0;

        for (Allotment allotment : activeAllotments) {

            try {

                demandService.generateDemand(
                        requestInfo,
                        allotment,
                        billingDate,
                        penaltyRate);

                generated++;

                log.info(
                        "Demand generated for allotment {}",
                        allotment.getAllotmentId());

            } catch (Exception e) {

                log.error(
                        "Failed for allotment {}: {}",
                        allotment.getAllotmentId(),
                        e.getMessage(),
                        e);
            }
        }

        String result =
                "Generated: " + generated +
                        ", Skipped: " + skipped;

        log.info("Scheduler done — {}", result);

        return result;
    }

    /**
     * Checks whether an allotment is active for the given billing date.
     *
     * @param allotment allotment details
     * @param billingDate billing date to validate
     * @return true if allotment is active; otherwise false
     */
    private boolean isActive(Allotment allotment, LocalDate billingDate) {
        if (allotment.getAgreementStartDate() == null
                || allotment.getAgreementStartDate().isAfter(billingDate)) {
            log.warn("Skipping allotment {} — startDate={} is null or after billingDate={}",
                    allotment.getAllotmentId(), allotment.getAgreementStartDate(), billingDate);
            return false;
        }
        if (allotment.getAgreementEndDate() != null
                && allotment.getAgreementEndDate().isBefore(billingDate)) {
            log.warn("Skipping allotment {} — endDate={} is before billingDate={}",
                    allotment.getAllotmentId(), allotment.getAgreementEndDate(), billingDate);
            return false;
        }
        return true;
    }

    /**
     * Determines whether demand generation is due for the allotment
     * based on its billing cycle and agreement dates.
     *
     * @param allotment allotment details
     * @param today current processing date
     * @return true if billing is due; otherwise false
     */
    private boolean isBillingDue(Allotment allotment, LocalDate today) {

        LocalDate startDate = allotment.getAgreementStartDate();

        if (startDate == null) {
            return false;
        }

        if (today.isBefore(startDate)) {
            return false;
        }

        if (allotment.getAgreementEndDate() != null
                && today.isAfter(allotment.getAgreementEndDate())) {
            return false;
        }

        int expectedBillingDay = Math.min(
                startDate.getDayOfMonth(),
                today.lengthOfMonth());

        BillingCycle cycle = BillingCycle.valueOf(
                allotment.getBillingCycle().toUpperCase(Locale.ROOT));

        switch (cycle) {

            case MONTHLY:

                return today.getDayOfMonth() == expectedBillingDay;

            case QUARTERLY:

                long months =
                        ChronoUnit.MONTHS.between(
                                startDate.withDayOfMonth(1),
                                today.withDayOfMonth(1));

                return today.getDayOfMonth() == expectedBillingDay
                        && months > 0
                        && months % 3 == 0;

            case YEARLY:

                return today.getDayOfMonth() == expectedBillingDay
                        && today.getMonth() == startDate.getMonth()
                        && today.getYear() > startDate.getYear();

            default:
                return false;
        }
    }
}
