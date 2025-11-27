package org.egov.ptr.models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class AdditionalFeeRate {

	BigDecimal rate;
	BigDecimal amount;
	BigDecimal minAmount;
	BigDecimal flatAmount;
	BigDecimal maxAmount;
	String fromFY;
	String startingDay;
	String active;
	String applicableAfterDays;
	String feeType;
}
