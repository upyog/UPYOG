package upyog.util;

import upyog.web.models.BillingCycle;
import upyog.web.models.BillingPeriod;

import java.time.LocalDate;

public class BillingPeriodUtil {

    /**
     * Determines the billing period for the given billing cycle.
     *
     * <p>For MONTHLY, QUARTERLY and YEARLY billing cycles, the period end date
     * is calculated based on the cycle duration. If the agreement end date falls
     * before the calculated period end date, the billing period is truncated to
     * the agreement end date to support partial cycle demand generation.</p>
     *
     * @param billingDate billing period start date
     * @param cycle billing cycle type
     * @param agreementEndDate agreement end date, if applicable
     * @return billing period containing start and end dates
     */
    public static BillingPeriod getBillingPeriod(
            LocalDate billingDate,
            BillingCycle cycle,
            LocalDate agreementEndDate) {

        LocalDate periodFrom = billingDate;
        LocalDate periodTo;

        switch (cycle) {

            case QUARTERLY:
                periodTo = billingDate.plusMonths(3).minusDays(1);
                break;

            case YEARLY:
                periodTo = billingDate.plusYears(1).minusDays(1);
                break;

            case MONTHLY:
            default:
                periodTo = billingDate.plusMonths(1).minusDays(1);
                break;
        }

        // If agreement ends before cycle completion,
        // generate partial cycle demand
        if (agreementEndDate != null
                && agreementEndDate.isBefore(periodTo)) {

            periodTo = agreementEndDate;
        }

        return new BillingPeriod(periodFrom, periodTo);
    }
}