package org.upyog.adv.web.models;

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
public class CalculationType {

	String applicationType;
	String serviceType;
	String feeType;
	BigDecimal amount;
	boolean taxApplicable;
}
