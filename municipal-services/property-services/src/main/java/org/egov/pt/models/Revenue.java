package org.egov.pt.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Revenue {

	BigDecimal totalTaxCollected = BigDecimal.ZERO;
	@JsonProperty("propertyTax")
	BigDecimal propertyTaxShare = BigDecimal.ZERO;
	@JsonProperty("refund")
	BigDecimal refundShare = BigDecimal.ZERO;
	@JsonProperty("penalty")
	BigDecimal penaltyShare = BigDecimal.ZERO;
	@JsonProperty("interest")
	BigDecimal interestShare = BigDecimal.ZERO;
	@JsonProperty("advance")
	BigDecimal advanceShare = BigDecimal.ZERO;
	@JsonProperty("arrears")
	BigDecimal arrearsShare = BigDecimal.ZERO;

}
