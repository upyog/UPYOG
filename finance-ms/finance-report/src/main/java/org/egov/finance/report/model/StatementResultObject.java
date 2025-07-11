/**
 * @author bpattanayak
 * @date 1 Jul 2025
 */

package org.egov.finance.report.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class StatementResultObject {
	
	 private static final BigDecimal NEGATIVE = new BigDecimal(-1);
	    BigDecimal amount;
	    Long fundId;
	    String fundCode;
	    String glCode = "";
	    Character type;
	    String scheduleNumber = "";
	    String scheduleName = "";
	    String majorCode;
	    BigDecimal budgetAmount;
	    
	    public boolean isLiability() {
	        return type != null ? "L".equalsIgnoreCase(type.toString()) : false;
	    }
	    
	    public void negateAmount() {
	        amount = amount.multiply(NEGATIVE);
	    }

}
