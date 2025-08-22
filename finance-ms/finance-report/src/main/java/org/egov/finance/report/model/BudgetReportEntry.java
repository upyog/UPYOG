
package org.egov.finance.report.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BudgetReportEntry {
    String glCode;
    BigDecimal budgetedAmtForYear = BigDecimal.ZERO;
    BigDecimal soFarAppropriated = BigDecimal.ZERO;
    BigDecimal balance = BigDecimal.ZERO;
    BigDecimal cumilativeIncludingCurrentBill = BigDecimal.ZERO;
    BigDecimal currentBalanceAvailable = BigDecimal.ZERO;
    BigDecimal currentBillAmount = BigDecimal.ZERO;
    String fundName;
    String functionName;
    String departmentName;
    String financialYear;
    String AccountCode;
    String budgetApprNumber;


}
