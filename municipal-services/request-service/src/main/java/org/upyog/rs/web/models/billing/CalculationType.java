package org.upyog.rs.web.models.billing;

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

	String code;
	String applicationType;
	String serviceType;
	String feeType;
	BigDecimal amount;
}
