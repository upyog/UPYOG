package org.egov.ptr.util;

import org.egov.ptr.models.AdditionalFeeRate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for calculating additional fees with comprehensive business rules
 * Handles ServiceCharge, PenaltyFee, and InterestAmount calculations
 */
@Slf4j
@Component
public class FeeCalculationUtil {

    /**
     * Calculates individual fee amount based on business rules
     *
     * @param feeConfig Fee configuration from MDMS
     * @param baseAmount Base amount for rate calculation
     * @param daysElapsed Days elapsed since application (for penalty/interest)
     * @param currentFY Current financial year
     * @return Calculated fee amount after applying all constraints
     */
    public BigDecimal calculateFeeAmount(AdditionalFeeRate feeConfig, BigDecimal baseAmount,
                                         int daysElapsed, String currentFY) {

        // Step 1: Validate business rules
        if (!isFeeApplicable(feeConfig, daysElapsed, currentFY)) {
            return BigDecimal.ZERO;
        }

        // Step 2: Determine base amount (flatAmount vs rate calculation)
        BigDecimal calculatedAmount = determineBaseAmount(feeConfig, baseAmount);

        // Step 3: Apply min/max constraints
        BigDecimal finalAmount = applyMinMaxConstraints(calculatedAmount, feeConfig);

        return finalAmount;
    }

    /**
     * Validates if fee is applicable based on business rules
     * This method is flexible and handles various MDMS configuration changes
     */
    private boolean isFeeApplicable(AdditionalFeeRate feeConfig, int daysElapsed, String currentFY) {
        // Check if fee is active (flexible handling of boolean/string values)
        if (!isActive(feeConfig)) {
            return false;
        }

        // Check financial year (flexible matching)
        if (!isFinancialYearApplicable(feeConfig, currentFY)) {
            return false;
        }

        // Check applicable after days (flexible parsing)
        if (!isDaysElapsedApplicable(feeConfig, daysElapsed)) {
            return false;
        }

        return true;
    }

    /**
     * Flexible active check - handles both boolean and string values
     */
    private boolean isActive(AdditionalFeeRate feeConfig) {
        if (feeConfig.getActive() == null) {
            return false;
        }

        String activeValue = feeConfig.getActive().toString().toLowerCase();
        return "true".equals(activeValue) || "1".equals(activeValue) || "yes".equals(activeValue);
    }

    /**
     * Flexible financial year check - handles various formats
     */
    private boolean isFinancialYearApplicable(AdditionalFeeRate feeConfig, String currentFY) {
        if (feeConfig.getFromFY() == null || feeConfig.getFromFY().trim().isEmpty()) {
            return true; // No FY restriction
        }

        String configFY = feeConfig.getFromFY().trim();
        String normalizedCurrentFY = currentFY.trim();

        // Direct match
        if (configFY.equals(normalizedCurrentFY)) {
            return true;
        }

        // Flexible matching for different formats
        // Handle cases like "2025-26" vs "2025-2026" vs "2025-26" vs "2025-26"
        return normalizeFinancialYear(configFY).equals(normalizeFinancialYear(normalizedCurrentFY));
    }

    /**
     * Normalizes financial year format for comparison
     */
    private String normalizeFinancialYear(String fy) {
        if (fy == null) return "";

        // Remove any extra spaces and convert to lowercase
        fy = fy.trim().toLowerCase();

        // Handle different formats: "2025-26", "2025-2026", "25-26", etc.
        if (fy.contains("-")) {
            String[] parts = fy.split("-");
            if (parts.length == 2) {
                String startYear = parts[0].trim();
                String endYear = parts[1].trim();

                // Normalize to 4-digit years
                if (startYear.length() == 2) {
                    startYear = "20" + startYear;
                }
                if (endYear.length() == 2) {
                    endYear = "20" + endYear;
                }

                return startYear + "-" + endYear.substring(2); // Return as "2025-26"
            }
        }

        return fy;
    }

    /**
     * Flexible days elapsed check - handles various formats
     */
    private boolean isDaysElapsedApplicable(AdditionalFeeRate feeConfig, int daysElapsed) {
        if (feeConfig.getApplicableAfterDays() == null || feeConfig.getApplicableAfterDays().trim().isEmpty()) {
            return true; // No days restriction
        }

        try {
            int requiredDays = Integer.parseInt(feeConfig.getApplicableAfterDays().trim());
            return daysElapsed >= requiredDays;
        } catch (NumberFormatException e) {
            return true; // If invalid, treat as no restriction
        }
    }

    /**
     * Determines base amount - flexible calculation based on MDMS configuration
     * Priority: flatAmount > rate calculation > amount field
     */
    private BigDecimal determineBaseAmount(AdditionalFeeRate feeConfig, BigDecimal baseAmount) {
        // Priority 1: Check for flatAmount first (highest priority)
        if (feeConfig.getFlatAmount() != null && feeConfig.getFlatAmount().compareTo(BigDecimal.ZERO) > 0) {
            return feeConfig.getFlatAmount();
        }

        // Priority 2: Calculate using rate
        if (feeConfig.getRate() != null && feeConfig.getRate().compareTo(BigDecimal.ZERO) > 0 && baseAmount != null) {
            return calculateRateBasedAmount(baseAmount, feeConfig.getRate());
        }

        // Priority 3: Use amount field as fallback
        if (feeConfig.getAmount() != null && feeConfig.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            return feeConfig.getAmount();
        }

        return BigDecimal.ZERO;
    }

    /**
     * Calculates amount based on rate percentage
     */
    private BigDecimal calculateRateBasedAmount(BigDecimal baseAmount, BigDecimal rate) {
        if (baseAmount == null || rate == null) {
            return BigDecimal.ZERO;
        }

        // Calculate fee as percentage of base amount
        BigDecimal calculatedAmount = baseAmount.multiply(rate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        // Round up to the next whole number (ceiling)
        return calculatedAmount.setScale(0, RoundingMode.CEILING);
    }

    /**
     * Applies min/max constraints to the calculated amount
     * Flexible handling of various constraint scenarios
     */
    private BigDecimal applyMinMaxConstraints(BigDecimal calculatedAmount, AdditionalFeeRate feeConfig) {
        if (calculatedAmount == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal finalAmount = calculatedAmount;
        BigDecimal minAmount = feeConfig.getMinAmount();
        BigDecimal maxAmount = feeConfig.getMaxAmount();

        // Validate min/max amounts
        if (minAmount != null && minAmount.compareTo(BigDecimal.ZERO) < 0) {
            minAmount = null;
        }

        if (maxAmount != null && maxAmount.compareTo(BigDecimal.ZERO) < 0) {
            maxAmount = null;
        }

        // Check for logical consistency
        if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
            minAmount = null;
            maxAmount = null;
        }

        // Apply constraints
        if (maxAmount != null && calculatedAmount.compareTo(maxAmount) > 0) {
            finalAmount = maxAmount;
        }

        if (minAmount != null && finalAmount.compareTo(minAmount) < 0) {
            finalAmount = minAmount;
        }

        return finalAmount;
    }

    /**
     * Calculates total additional fees for all fee types
     *
     * @param feeConfigs List of fee configurations
     * @param baseAmount Base amount for rate calculations
     * @param daysElapsed Days elapsed since application
     * @param currentFY Current financial year
     * @return Total additional fees
     */
    public BigDecimal calculateTotalAdditionalFees(List<AdditionalFeeRate> feeConfigs, BigDecimal baseAmount,
                                                   int daysElapsed, String currentFY) {
        BigDecimal totalFees = BigDecimal.ZERO;

        for (AdditionalFeeRate feeConfig : feeConfigs) {
            BigDecimal feeAmount = calculateFeeAmount(feeConfig, baseAmount, daysElapsed, currentFY);
            totalFees = totalFees.add(feeAmount);
        }

        return totalFees;
    }

    /**
     * Gets current financial year in MDMS format (e.g., "2025-26")
     */
    public String getCurrentFinancialYear() {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();

        // Financial year starts from April (month 4)
        if (currentMonth >= 4) {
            return currentYear + "-" + String.valueOf(currentYear + 1).substring(2);
        } else {
            return (currentYear - 1) + "-" + String.valueOf(currentYear).substring(2);
        }
    }

    /**
     * Calculates days elapsed since a given date
     */
    public int calculateDaysElapsed(long applicationDateMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedMillis = currentTimeMillis - applicationDateMillis;
        return (int) (elapsedMillis / (24 * 60 * 60 * 1000));
    }

    /**
     * Calculates days from validity date to application creation date
     * Used for penalty calculation in renewal applications
     *
     * @param validityDateMillis Validity date in milliseconds (from previous application)
     * @param applicationDateMillis Application creation date in milliseconds
     * @return Number of days from validity date to application date (can be negative if before validity date)
     */
    public int calculateDaysFromValidityDate(long validityDateMillis, long applicationDateMillis) {
        long elapsedMillis = applicationDateMillis - validityDateMillis;
        return (int) (elapsedMillis / (24 * 60 * 60 * 1000));
    }

    /**
     * Calculates tiered penalty based on days from validity date
     * The penalty is calculated by summing up penalties for each tier range based on MDMS configuration.
     *
     * Example MDMS Configuration:
     * - startingDay: "1", flatAmount: 100  -> Days 1-30: 100rs per month
     * - startingDay: "31", flatAmount: 200 -> Days 31-60: 200rs per month
     * - startingDay: "61", flatAmount: 500 -> Days 61+: 500rs per month
     *
     * @param penaltyConfigs List of penalty configurations from MDMS (should be sorted by startingDay)
     * @param daysFromValidityDate Days elapsed from validity date
     * @param currentFY Current financial year
     * @return Calculated penalty amount
     */
    public BigDecimal calculateTieredPenalty(List<AdditionalFeeRate> penaltyConfigs,
                                             int daysFromValidityDate, String currentFY) {

        // If days are negative or zero, no penalty
        if (daysFromValidityDate <= 0) {
            return BigDecimal.ZERO;
        }

        // If no configurations from MDMS, return zero (no default penalty)
        if (penaltyConfigs == null || penaltyConfigs.isEmpty()) {
            log.warn("No penalty configurations from MDMS, returning zero penalty");
            return BigDecimal.ZERO;
        }

        // Sort configurations by startingDay (ascending)
        List<AdditionalFeeRate> sortedConfigs = new ArrayList<>(penaltyConfigs);
        sortedConfigs.sort((a, b) -> {
            int dayA = parseStartingDay(a.getStartingDay());
            int dayB = parseStartingDay(b.getStartingDay());
            return Integer.compare(dayA, dayB);
        });

        // Calculate penalty by summing up each tier
        BigDecimal totalPenalty = BigDecimal.ZERO;
        int previousDay = 0; // Start from day 0 (no grace period)

        for (int i = 0; i < sortedConfigs.size(); i++) {
            AdditionalFeeRate config = sortedConfigs.get(i);

            if (!isTieredPenaltyApplicable(config, daysFromValidityDate, currentFY)) {
                continue;
            }

            int startingDay = parseStartingDay(config.getStartingDay());
            BigDecimal penaltyPerMonth = config.getFlatAmount();

            if (penaltyPerMonth == null || penaltyPerMonth.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // Determine the end day for this tier (next tier's starting day or daysFromValidityDate)
            int endDay = daysFromValidityDate;

            // Look for the next applicable tier
            if (i + 1 < sortedConfigs.size()) {
                int nextStartingDay = parseStartingDay(sortedConfigs.get(i + 1).getStartingDay());
                if (nextStartingDay <= daysFromValidityDate) {
                    endDay = nextStartingDay - 1; // End one day before next tier starts
                }
            }

            // Calculate days in this tier
            int daysInTier = Math.max(0, endDay - Math.max(previousDay, startingDay - 1));

            if (daysInTier > 0) {
                // Calculate months (rounded up)
                int months = (int) Math.ceil(daysInTier / 30.0);
                BigDecimal tierPenalty = penaltyPerMonth.multiply(new BigDecimal(months));
                totalPenalty = totalPenalty.add(tierPenalty);

                log.debug("Tier {}: Days {}-{} ({} days = {} months) @ {}rs/month = {}rs",
                        i + 1, Math.max(previousDay + 1, startingDay), endDay, daysInTier, months,
                        penaltyPerMonth, tierPenalty);
            }

            previousDay = Math.max(previousDay, endDay);
        }

        return totalPenalty;
    }

    /**
     * Parses startingDay from configuration (handles string to int conversion)
     */
    private int parseStartingDay(String startingDay) {
        if (startingDay == null || startingDay.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(startingDay.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid startingDay value: {}, defaulting to 0", startingDay);
            return 0;
        }
    }

    /**
     * Checks if tiered penalty configuration is applicable
     * This method is specifically for tiered penalty calculation
     */
    private boolean isTieredPenaltyApplicable(AdditionalFeeRate feeConfig, int daysElapsed, String currentFY) {
        // Check if fee is active
        if (!isActive(feeConfig)) {
            return false;
        }

        // Check financial year (flexible matching)
        if (!isFinancialYearApplicable(feeConfig, currentFY)) {
            return false;
        }

        // For tiered penalty, we check startingDay instead of applicableAfterDays
        int startingDay = parseStartingDay(feeConfig.getStartingDay());
        return daysElapsed >= startingDay;
    }
}