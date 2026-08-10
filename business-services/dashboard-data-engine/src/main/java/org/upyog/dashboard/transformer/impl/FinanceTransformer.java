package org.upyog.dashboard.transformer.impl;

import java.util.List;

import org.springframework.stereotype.Component;
import org.upyog.dashboard.finance.dto.FinanceAggregatedData;
import org.upyog.dashboard.finance.dto.FinanceDTO;
import org.upyog.dashboard.finance.model.FinanceMetric;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.transformer.ModuleTransformer;

@Component
public class FinanceTransformer implements ModuleTransformer<FinanceDTO> {

    @Override
    public Module getModule() {
        return Module.FINANCE;
    }

    @Override
    public DashboardPayload transform(FinanceDTO rawData) {
        FinanceAggregatedData combined = rawData.getCombinedMetrics();
        
        if (combined == null) {
            combined = new FinanceAggregatedData();
        }

        FinanceMetric financeMetric = FinanceMetric.builder()
                .totalRevenueCollected(combined.getTotalRevenueCollected())
                .totalAuditsCompleted(combined.getTotalAuditsCompleted())
                .totalAudits(combined.getTotalAudits())
                .totalOutstandingDebt(combined.getTotalOutstandingDebt())
                .totalCurrentExpenditure(combined.getTotalCurrentExpenditure())
                .totalInterestExpenses(combined.getTotalInterestExpenses())
                .totalBills(combined.getTotalBills())
                .pendingBills(combined.getPendingBills())
                .totalFundBalance(combined.getTotalFundBalance())
                .totalFundRequirement(combined.getTotalFundRequirement())
                .build();

        DashboardData dashboardData = DashboardData.builder()
                .date(rawData.getDate())
                .module(rawData.getModule())
                .ward(rawData.getWard())
                .ulb(rawData.getUlb())
                .region(rawData.getRegion())
                .state(rawData.getState())
                .metrics(financeMetric.toMap())
                .build();

        return DashboardPayload.builder()
                .data(List.of(dashboardData))
                .build();
    }
}
