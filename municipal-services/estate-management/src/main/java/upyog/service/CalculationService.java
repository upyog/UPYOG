package upyog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import upyog.config.ServiceConstants;
import upyog.web.models.Allotment;
import upyog.web.models.BillingCycle;
import upyog.web.models.billing.DemandDetail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class CalculationService {

    /**
     * Creates booking fee demand details for the calculated rent amount.
     *
     * @param amount calculated rent amount for the billing period
     * @param allotment allotment details for which demand is being generated
     * @return list containing booking fee demand detail
     */
    public List<DemandDetail> calculateDemand(BigDecimal amount, Allotment allotment) {
        DemandDetail detail = DemandDetail.builder()
                .taxHeadMasterCode(ServiceConstants.EST_BOOKING_FEE)
                .taxAmount(amount)
                .collectionAmount(BigDecimal.ZERO)
                .tenantId(allotment.getTenantId())
                .build();
        log.info("Booking fee demand detail - Amount: {}", allotment.getMonthlyRent());
        return List.of(detail);
    }

    /**
     * Calculates rent amount for the given billing period based on the configured
     * billing cycle of the allotment.
     *
     * <p>
     * Supported billing cycles:
     * <ul>
     *     <li>MONTHLY - Calculates full or partial monthly rent.</li>
     *     <li>QUARTERLY - Calculates full quarter rent (3 months) or proportional
     *     rent for a partial quarter.</li>
     *     <li>YEARLY - Calculates full year rent (12 months) or proportional
     *     rent for a partial year.</li>
     * </ul>
     * </p>
     *
     * <p>
     * Partial rent is calculated proportionally based on the number of chargeable
     * days within the billing period.
     * </p>
     *
     * @param allotment allotment details containing rent and billing cycle information
     * @param periodFrom billing period start date
     * @param periodTo billing period end date
     * @return calculated rent amount for the billing period
     */
    public BigDecimal calculateAmount(
            Allotment allotment,
            LocalDate periodFrom,
            LocalDate periodTo) {

        BillingCycle cycle =
                BillingCycle.valueOf(
                        allotment.getBillingCycle().toUpperCase(Locale.ROOT));

        BigDecimal monthlyRent = allotment.getMonthlyRent();

        switch (cycle) {

            case QUARTERLY:

                LocalDate quarterEnd =
                        periodFrom.plusMonths(3).minusDays(1);

                // Full quarter
                if (quarterEnd.equals(periodTo)) {
                    return monthlyRent.multiply(BigDecimal.valueOf(3));
                }

                // Partial quarter
                long quarterChargeableDays =
                        ChronoUnit.DAYS.between(periodFrom, periodTo) + 1;

                long totalQuarterDays =
                        ChronoUnit.DAYS.between(periodFrom, quarterEnd) + 1;

                return monthlyRent.multiply(BigDecimal.valueOf(3))
                        .multiply(BigDecimal.valueOf(quarterChargeableDays))
                        .divide(
                                BigDecimal.valueOf(totalQuarterDays),
                                2,
                                RoundingMode.HALF_UP);

            case YEARLY:

                LocalDate yearEnd =
                        periodFrom.plusYears(1).minusDays(1);

                // Full year
                if (yearEnd.equals(periodTo)) {
                    return monthlyRent.multiply(BigDecimal.valueOf(12));
                }

                // Partial year
                long yearChargeableDays =
                        ChronoUnit.DAYS.between(periodFrom, periodTo) + 1;

                long totalYearDays =
                        ChronoUnit.DAYS.between(periodFrom, yearEnd) + 1;

                return monthlyRent.multiply(BigDecimal.valueOf(12))
                        .multiply(BigDecimal.valueOf(yearChargeableDays))
                        .divide(
                                BigDecimal.valueOf(totalYearDays),
                                2,
                                RoundingMode.HALF_UP);

            case MONTHLY:
            default:

                LocalDate expectedEnd =
                        periodFrom.plusMonths(1).minusDays(1);

                // Full month
                if (expectedEnd.equals(periodTo)) {
                    return monthlyRent;
                }

                // Partial month
                long chargeableDays =
                        ChronoUnit.DAYS.between(periodFrom, periodTo) + 1;

                long totalDaysInMonth =
                        YearMonth.from(periodFrom).lengthOfMonth();

                return monthlyRent
                        .divide(
                                BigDecimal.valueOf(totalDaysInMonth),
                                2,
                                RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(chargeableDays));
        }
    }
}
