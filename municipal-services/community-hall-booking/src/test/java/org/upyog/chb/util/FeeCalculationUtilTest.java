package org.upyog.chb.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.upyog.chb.web.models.AdditionalFeeRate;

/**
 * Test class for FeeCalculationUtil
 */
public class FeeCalculationUtilTest {

    private FeeCalculationUtil feeCalculationUtil;

    @BeforeEach
    public void setUp() {
        feeCalculationUtil = new FeeCalculationUtil();
    }

    @Test
    public void testGetCurrentFinancialYear() {
        // Test returns format YYYY-YY
        String fy = feeCalculationUtil.getCurrentFinancialYear();
        assertNotNull(fy);
        assertTrue(fy.matches("\\d{4}-\\d{2}"));

        // For September 2025, should return 2025-26 (since financial year starts April)
        assertEquals("2025-26", fy);
    }

    @Test
    public void testCalculateFeeAmountWithFlatAmount() {
        AdditionalFeeRate serviceCharge = AdditionalFeeRate.builder()
                .feeType("ServiceCharge")
                .flatAmount(new BigDecimal("500"))
                .fromFY("2025-26")
                .applicableAfterDays("0")
                .active(true)
                .taxApplicable(true)
                .build();

        BigDecimal result = feeCalculationUtil.calculateFeeAmount(
                serviceCharge, new BigDecimal("7500"), 0, "2025-26");

        assertEquals(new BigDecimal("500"), result);
    }

    @Test
    public void testCalculateFeeAmountWithRate() {
        AdditionalFeeRate interestAmount = AdditionalFeeRate.builder()
                .feeType("InterestAmount")
                .rate(new BigDecimal("2"))
                .minAmount(new BigDecimal("50"))
                .maxAmount(new BigDecimal("500"))
                .fromFY("2025-26")
                .applicableAfterDays("7")
                .active(true)
                .taxApplicable(false)
                .build();

        // Test normal calculation: 7500 * 2% = 150
        BigDecimal result = feeCalculationUtil.calculateFeeAmount(
                interestAmount, new BigDecimal("7500"), 7, "2025-26");

        assertEquals(new BigDecimal("150.00"), result);
    }

    @Test
    public void testCalculateFeeAmountWithMinConstraint() {
        AdditionalFeeRate interestAmount = AdditionalFeeRate.builder()
                .feeType("InterestAmount")
                .rate(new BigDecimal("2"))
                .minAmount(new BigDecimal("50"))
                .maxAmount(new BigDecimal("500"))
                .fromFY("2025-26")
                .applicableAfterDays("7")
                .active(true)
                .taxApplicable(false)
                .build();

        // Test below minimum: 1000 * 2% = 20, but min is 50
        BigDecimal result = feeCalculationUtil.calculateFeeAmount(
                interestAmount, new BigDecimal("1000"), 7, "2025-26");

        assertEquals(new BigDecimal("50"), result);
    }

    @Test
    public void testCalculateFeeAmountWithMaxConstraint() {
        AdditionalFeeRate interestAmount = AdditionalFeeRate.builder()
                .feeType("InterestAmount")
                .rate(new BigDecimal("2"))
                .minAmount(new BigDecimal("50"))
                .maxAmount(new BigDecimal("500"))
                .fromFY("2025-26")
                .applicableAfterDays("7")
                .active(true)
                .taxApplicable(false)
                .build();

        // Test above maximum: 50000 * 2% = 1000, but max is 500
        BigDecimal result = feeCalculationUtil.calculateFeeAmount(
                interestAmount, new BigDecimal("50000"), 7, "2025-26");

        assertEquals(new BigDecimal("500"), result);
    }

    @Test
    public void testIsFeeApplicableForDays() {
        AdditionalFeeRate penaltyFee = AdditionalFeeRate.builder()
                .feeType("PenaltyFee")
                .applicableAfterDays("1")
                .active(true)
                .build();

        // Should not be applicable for 0 days (event hasn't occurred)
        assertFalse(feeCalculationUtil.isFeeApplicableForDays(penaltyFee, 0));

        // Should be applicable for 1 or more days
        assertTrue(feeCalculationUtil.isFeeApplicableForDays(penaltyFee, 1));
        assertTrue(feeCalculationUtil.isFeeApplicableForDays(penaltyFee, 5));
    }

    @Test
    public void testFeeNotApplicableWhenInactive() {
        AdditionalFeeRate inactiveFee = AdditionalFeeRate.builder()
                .feeType("TestFee")
                .flatAmount(new BigDecimal("100"))
                .active(false)
                .fromFY("2025-26")
                .applicableAfterDays("0")
                .build();

        BigDecimal result = feeCalculationUtil.calculateFeeAmount(
                inactiveFee, new BigDecimal("1000"), 0, "2025-26");

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void testFeeNotApplicableForWrongFY() {
        AdditionalFeeRate futureFee = AdditionalFeeRate.builder()
                .feeType("TestFee")
                .flatAmount(new BigDecimal("100"))
                .active(true)
                .fromFY("2026-27")
                .applicableAfterDays("0")
                .build();

        BigDecimal result = feeCalculationUtil.calculateFeeAmount(
                futureFee, new BigDecimal("1000"), 0, "2025-26");

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void testCalculateDaysElapsed() {
        // Test with a date 5 days ago
        long fiveDaysAgoMillis = System.currentTimeMillis() - (5L * 24 * 60 * 60 * 1000);
        int daysElapsed = feeCalculationUtil.calculateDaysElapsed(fiveDaysAgoMillis);
        assertEquals(5, daysElapsed);

        // Test with future date (should return 0)
        long futureMillis = System.currentTimeMillis() + (2L * 24 * 60 * 60 * 1000);
        int futureDays = feeCalculationUtil.calculateDaysElapsed(futureMillis);
        assertEquals(0, futureDays);
    }
}