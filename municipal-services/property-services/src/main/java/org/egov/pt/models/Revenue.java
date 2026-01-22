package org.egov.pt.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
	@JsonIgnore
	//@JsonProperty("taxCollectedProperties")
	List<TaxCollectedProperties> TaxCollectedProperties;

}
