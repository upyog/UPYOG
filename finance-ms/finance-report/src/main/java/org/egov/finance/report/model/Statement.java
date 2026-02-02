/**
 * @author bpattanayak
 * @date 30 Jun 2025
 */

package org.egov.finance.report.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.egov.finance.report.entity.Boundary;
import org.egov.finance.report.entity.Department;
import org.egov.finance.report.entity.FinancialYear;
import org.egov.finance.report.entity.Function;
import org.egov.finance.report.entity.Functionary;
import org.egov.finance.report.entity.Fund;

import lombok.Data;

@Data
public class Statement {
	
	private String period;
    private FinancialYear financialYear;
    private Date asOndate;
    private Date fromDate;
    private Date toDate;
    private String currency;
    private BigDecimal currencyInAmount;
    private Department department;
    private Functionary functionary;
    private Function function;
    private Boundary field;
    private Fund fund;
    private List<Fund> fundList = new ArrayList<Fund>();
    private List<IEStatementEntry> ieEntries = new ArrayList<IEStatementEntry>();
    private List<StatementEntry> entries = new ArrayList<StatementEntry>();
    
    public BigDecimal getDivisor() {
        if ("Thousands".equalsIgnoreCase(currency))
            return new BigDecimal(1000);
        if ("Lakhs".equalsIgnoreCase(currency))
            return new BigDecimal(100000);
        return BigDecimal.ONE;
    }
    
    public void add(final StatementEntry entry) {
        entries.add(entry);
    }
    
    public StatementEntry get(final int index) {
        return entries.get(index);
    }
    
    public boolean containsBalanceSheetEntry(final String glCode) {
        if (glCode == null)
            return false;
        for (final StatementEntry balanceSheetEntry : getEntries())
            if (balanceSheetEntry.getGlCode() != null && glCode.equals(balanceSheetEntry.getGlCode()))
                return true;
        return false;
    }
    
    public int size() {
        return entries.size();
    }
    
    public void addAll(final Statement balanceSheet) {
        entries.addAll(balanceSheet.getEntries());
    }
    
    public void setCurrency(final String currency) {
        this.currency = currency;
        if (this.currency.equalsIgnoreCase("rupees"))
            currencyInAmount = new BigDecimal(1);
        if (this.currency.equalsIgnoreCase("thousands"))
            currencyInAmount = new BigDecimal(1000);
        if (this.currency.equalsIgnoreCase("lakhs"))
            currencyInAmount = new BigDecimal(100000);
    }

}
