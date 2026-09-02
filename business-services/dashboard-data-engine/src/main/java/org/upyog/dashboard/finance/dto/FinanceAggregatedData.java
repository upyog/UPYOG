package org.upyog.dashboard.finance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinanceAggregatedData {
    private Integer totalRevenueCollected;
    private Integer totalAuditsCompleted;
    private Integer totalAudits;
    private Integer totalOutstandingDebt;
    private Integer totalCurrentExpenditure;
    private Integer totalInterestExpenses;
    private Integer totalBills;
    private Integer pendingBills;
    private Integer totalFundBalance;
    private Integer totalFundRequirement;
}
