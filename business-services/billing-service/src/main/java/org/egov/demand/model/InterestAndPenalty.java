package org.egov.demand.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class InterestAndPenalty {
	private String billDetailsId;
	private BigDecimal initialTotalAmountCalculated;
	private BigDecimal updatedTotalAmountCalculated;
	private String type;
	private String fromDate;
	//For Reference
	private String IntialtoDate;
	//To date will be calculated form updatetoDate
	private String updatetoDate;
	private Long noOfdays;
	

}
