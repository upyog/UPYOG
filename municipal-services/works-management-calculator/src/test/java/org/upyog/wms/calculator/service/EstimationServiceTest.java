package org.upyog.wms.calculator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.upyog.wms.calculator.web.models.ActionPlan;
import org.upyog.wms.calculator.web.models.Component;
import org.upyog.wms.calculator.web.models.FinancialSummary;
import org.upyog.wms.calculator.web.models.FundDetail;
import org.upyog.wms.calculator.web.models.TaxHeadEstimate;
import org.upyog.wms.calculator.web.models.mdms.FeeConfig;

class EstimationServiceTest {

    private final EstimationService service = new EstimationService();

    private static FundDetail fund(String type, String source, String amount) {
        return FundDetail.builder().fundType(type).sourceCode(source).amount(new BigDecimal(amount)).build();
    }

    private static Component component(String no, String category, String amount, FundDetail fund) {
        return Component.builder().componentNumber(no).componentName(no).category(category).status("ACTIVE")
                .approvedAmount(new BigDecimal(amount)).fundDetails(List.of(fund)).build();
    }

    /** FRS Section 5 example: 100 = Manpower 20 + Material 40 + Service 40. */
    private static ActionPlan frsExample() {
        return ActionPlan.builder().tenantId("od.cuttack").applicationNumber("WMS-AP-1")
                .approvedAmount(new BigDecimal("100")).fundSource("PARTIAL")
                .fundDetails(List.of(fund("GOVT_GRANT", "CENTRAL_GRANT", "60"), fund("OSR", "PROPERTY_TAX", "40")))
                .components(List.of(
                        component("C-1", "MANPOWER", "20", fund("GOVT_GRANT", "CENTRAL_GRANT", "20")),
                        component("C-2", "GOODS", "40", fund("GOVT_GRANT", "CENTRAL_GRANT", "40")),
                        component("C-3", "SERVICES", "40", fund("OSR", "PROPERTY_TAX", "40"))))
                .build();
    }

    @Test
    void frsExampleIsBalancedWithPercentages() {
        FinancialSummary s = service.summarize(frsExample());
        assertTrue(s.balanced(), s.observations()::toString);
        assertEquals(new BigDecimal("100.00"), s.totalComponentAmount());
        assertEquals(new BigDecimal("0.00"), s.unallocatedBalance());
        assertEquals(List.of(new BigDecimal("20.00"), new BigDecimal("40.00"), new BigDecimal("40.00")),
                s.componentBreakup().stream().map(FinancialSummary.ComponentBreakup::percentage).toList());
        assertEquals(3, s.categoryBreakup().size());
        assertEquals(new BigDecimal("60.00"), s.componentFundBreakup().get(0).amount());
        assertEquals("CENTRAL_GRANT", s.planFundBreakup().get(0).sourceCode());
    }

    @Test
    void unallocatedBalanceIsReported() {
        ActionPlan plan = frsExample();
        plan.setApprovedAmount(new BigDecimal("120"));
        FinancialSummary s = service.summarize(plan);
        assertFalse(s.balanced());
        assertEquals(new BigDecimal("20.00"), s.unallocatedBalance());
        assertEquals(new BigDecimal("16.67"), s.componentBreakup().get(0).percentage());
    }

    @Test
    void inactiveComponentsAreExcluded() {
        ActionPlan plan = frsExample();
        Component removed = component("C-4", "GOODS", "500", fund("GOVT_GRANT", "CENTRAL_GRANT", "500"));
        removed.setStatus("INACTIVE");
        plan.setComponents(new java.util.ArrayList<>(plan.getComponents()));
        plan.getComponents().add(removed);
        assertTrue(service.summarize(plan).balanced());
    }

    @Test
    void feesFollowMdmsConfiguration() {
        List<FeeConfig> configs = List.of(
                new FeeConfig("PCT", "WMS.ACTION_PLAN", "WMS_AP_PROCESSING_FEE", "PERCENTAGE",
                        new BigDecimal("0.5"), new BigDecimal("100"), null, List.of(), true),
                new FeeConfig("FLAT_OFF", "WMS.ACTION_PLAN", "WMS_AP_FLAT_FEE", "FLAT",
                        new BigDecimal("250"), null, null, List.of(), false),
                new FeeConfig("GRANT_ONLY", "WMS.ACTION_PLAN", "WMS_AP_GRANT_FEE", "FLAT",
                        new BigDecimal("50"), null, null, List.of("GOVT_GRANT"), true));
        List<TaxHeadEstimate> fees = service.fees(frsExample(), configs, "WMS.ACTION_PLAN");
        assertEquals(1, fees.size());
        assertEquals(new BigDecimal("100.00"), fees.get(0).estimateAmount());   // 0.5% of 100 = 0.50 -> min 100
    }

    @Test
    void noActiveFeeMeansNoFee() {
        assertTrue(service.fees(frsExample(), List.of(), "WMS.ACTION_PLAN").isEmpty());
    }
}
