package org.upyog.chb.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.upyog.chb.web.models.AdditionalFeeRate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FeeCalculationUtil {

    /**
     * Get current financial year in format "YYYY-YY"
     * Financial year starts from April 1st
     */
    public String getCurrentFinancialYear() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based

        if (month >= 4) {
            // Current financial year: April YYYY to March YYYY+1
            return String.format("%d-%02d", year, (year + 1) % 100);
        } else {
            // Previous financial year: April YYYY-1 to March YYYY
            return String.format("%d-%02d", year - 1, year % 100);
        }
    }

    /**
     * Check if a fee rate is active and applicable for the given financial year
     */
    public boolean isFeeApplicable(AdditionalFeeRate feeRate, String currentFY) {
        // Check if active
        if (feeRate.getActive() instanceof Boolean) {
            if (!((Boolean) feeRate.getActive())) {
                return false;
            }
        } else if (feeRate.getActive() instanceof String) {
            if (!"true".equalsIgnoreCase((String) feeRate.getActive())) {
                return false;
            }
        }

        // Check financial year
        if (feeRate.getFromFY() != null && !feeRate.getFromFY().equals(currentFY)) {
            return false;
        }

        return true;
    }

    /**
     * Check if fee is applicable based on days elapsed
     */
    public boolean isFeeApplicableForDays(AdditionalFeeRate feeRate, int daysElapsed) {
        if (feeRate.getApplicableAfterDays() == null) {
            return true;
        }

        try {
            int requiredDays = Integer.parseInt(feeRate.getApplicableAfterDays());
            return daysElapsed >= requiredDays;
        } catch (NumberFormatException e) {
            log.warn("Invalid applicableAfterDays value: {}", feeRate.getApplicableAfterDays());
            return true;
        }
    }

    /**
     * Calculate fee amount based on flat amount or rate
     * Applies min/max constraints
     */
    public BigDecimal calculateFeeAmount(AdditionalFeeRate feeRate, BigDecimal baseAmount,
            int daysElapsed, String currentFY) {

        // Check if fee is applicable
        if (!isFeeApplicable(feeRate, currentFY)) {
            log.debug("Fee {} not applicable for FY {}", feeRate.getFeeType(), currentFY);
            return BigDecimal.ZERO;
        }

        if (!isFeeApplicableForDays(feeRate, daysElapsed)) {
            log.debug("Fee {} not applicable for {} days elapsed", feeRate.getFeeType(), daysElapsed);
            return BigDecimal.ZERO;
        }

        BigDecimal calculatedAmount;

        // Priority 1: Use flatAmount if available
        if (feeRate.getFlatAmount() != null) {
            calculatedAmount = feeRate.getFlatAmount();
            log.debug("Using flat amount: {} for fee: {}", calculatedAmount, feeRate.getFeeType());
        }
        // Priority 2: Calculate using rate
        else if (feeRate.getRate() != null && baseAmount != null) {
            calculatedAmount = baseAmount.multiply(feeRate.getRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING);
            log.debug("Calculated amount: {} (base: {}, rate: {}%) for fee: {}",
                    calculatedAmount, baseAmount, feeRate.getRate(), feeRate.getFeeType());
        }
        // No calculation possible
        else {
            log.warn("No flat amount or rate specified for fee: {}", feeRate.getFeeType());
            return BigDecimal.ZERO;
        }

        // Apply min/max constraints
        calculatedAmount = applyMinMaxConstraints(calculatedAmount, feeRate);

        return calculatedAmount;
    }

    /**
     * Apply minimum and maximum constraints to the calculated amount
     */
    private BigDecimal applyMinMaxConstraints(BigDecimal amount, AdditionalFeeRate feeRate) {
        BigDecimal constrainedAmount = amount;

        // Apply minimum constraint
        if (feeRate.getMinAmount() != null && constrainedAmount.compareTo(feeRate.getMinAmount()) < 0) {
            log.debug("Amount {} below minimum {}, using minimum for fee: {}",
                    constrainedAmount, feeRate.getMinAmount(), feeRate.getFeeType());
            constrainedAmount = feeRate.getMinAmount();
        }

        // Apply maximum constraint
        if (feeRate.getMaxAmount() != null && constrainedAmount.compareTo(feeRate.getMaxAmount()) > 0) {
            log.debug("Amount {} above maximum {}, using maximum for fee: {}",
                    constrainedAmount, feeRate.getMaxAmount(), feeRate.getFeeType());
            constrainedAmount = feeRate.getMaxAmount();
        }

        return constrainedAmount;
    }

    /**
     * Calculate days elapsed since a given date
     * Returns 0 if the date is in the future
     */
    public int calculateDaysElapsed(long fromDateMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedMillis = currentTimeMillis - fromDateMillis;
        int daysElapsed = (int) (elapsedMillis / (24 * 60 * 60 * 1000));

        // If date is in the future, return 0
        return Math.max(0, daysElapsed);
    }

    /**
     * Filter applicable fee rates from a list
     */
    public List<AdditionalFeeRate> getApplicableFeeRates(List<AdditionalFeeRate> feeRates,
            int daysElapsed, String currentFY) {
        return feeRates.stream()
                .filter(rate -> isFeeApplicable(rate, currentFY))
                .filter(rate -> isFeeApplicableForDays(rate, daysElapsed))
                .collect(Collectors.toList());
    }
}