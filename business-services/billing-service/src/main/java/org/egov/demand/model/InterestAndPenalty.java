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
	private BigDecimal interestonamount;
	private String adjusmentfromdate;
	private String assesmentyear;
	private String adjustedtosession;
	private String interestcalculatedsession;
	private BigDecimal interestpercentage;
	private int interestfornoofdays;
	private BigDecimal  totalAMountForInterest;
	private boolean previousYear;
	

}
