package org.upyog.cdwm.calculator.web.models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalculationType {
	
		String code;
		String applicationType;
		String serviceType;
		String feeType;
		BigDecimal amount;

}
