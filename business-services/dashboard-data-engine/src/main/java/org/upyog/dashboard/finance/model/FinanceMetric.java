package org.upyog.dashboard.finance.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceMetric {
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

    public Map<String, Object> toMap() {
        Map<String, Object> dataMap = new java.util.LinkedHashMap<>();
        dataMap.put("totalRevenueCollected", totalRevenueCollected != null ? totalRevenueCollected : 0);
        dataMap.put("totalAuditsCompleted", totalAuditsCompleted != null ? totalAuditsCompleted : 0);
        dataMap.put("totalAudits", totalAudits != null ? totalAudits : 0);
        dataMap.put("totalOutstandingDebt", totalOutstandingDebt != null ? totalOutstandingDebt : 0);
        dataMap.put("totalCurrentExpenditure", totalCurrentExpenditure != null ? totalCurrentExpenditure : 0);
        dataMap.put("totalInterestExpenses", totalInterestExpenses != null ? totalInterestExpenses : 0);
        dataMap.put("totalBills", totalBills != null ? totalBills : 0);
        dataMap.put("pendingBills", pendingBills != null ? pendingBills : 0);
        dataMap.put("totalFundBalance", totalFundBalance != null ? totalFundBalance : 0);
        dataMap.put("totalFundRequirement", totalFundRequirement != null ? totalFundRequirement : 0);
        return dataMap;
    }
}
