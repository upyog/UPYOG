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
public class StatementEntry {
	
	private String glCode;
    private String accountName;
    private String scheduleNo;
    // FundCode is added for RP report
    private String fundCode;
    private BigDecimal previousYearTotal = BigDecimal.ZERO;
    private BigDecimal currentYearTotal = BigDecimal.ZERO;
    private Map<String, BigDecimal> fundWiseAmount = new HashMap<String, BigDecimal>();
    private boolean displayBold = false;

}
