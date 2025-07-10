/**
 * @author bpattanayak
 * @date 30 Jun 2025
 */

package org.egov.finance.report.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IEStatementEntry {
	
	private String glCode;
    private String accountName;
    private String scheduleNo;
    private BigDecimal budgetAmount;
    private String majorCode;
    private final Map<String, BigDecimal> scheduleWiseTotal = new HashMap<String, BigDecimal>();
    private Map<String, BigDecimal> netAmount = new HashMap<String, BigDecimal>();
    private Map<String, BigDecimal> previousYearAmount = new HashMap<String, BigDecimal>();

    private boolean displayBold = false;

}
