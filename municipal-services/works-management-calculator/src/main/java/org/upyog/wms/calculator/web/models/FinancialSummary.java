package org.upyog.wms.calculator.web.models;

import java.math.BigDecimal;
import java.util.List;

/**
 * FRS Section 5: Total Approved Amount, component-wise break-up (with % of total), category break-up
 * (Manpower / Goods / Services ...) and funding-wise break-up.
 */
public record FinancialSummary(
        BigDecimal totalApprovedAmount,
        BigDecimal totalComponentAmount,
        BigDecimal unallocatedBalance,
        boolean balanced,
        List<ComponentBreakup> componentBreakup,
        List<CategoryBreakup> categoryBreakup,
        List<FundBreakup> planFundBreakup,
        List<FundBreakup> componentFundBreakup,
        List<String> observations) {

    public record ComponentBreakup(String componentNumber, String componentName, String category,
                                   BigDecimal amount, BigDecimal percentage) {
    }

    public record CategoryBreakup(String category, BigDecimal amount, BigDecimal percentage) {
    }

    public record FundBreakup(String fundType, String sourceCode, BigDecimal amount, BigDecimal percentage) {
    }
}
