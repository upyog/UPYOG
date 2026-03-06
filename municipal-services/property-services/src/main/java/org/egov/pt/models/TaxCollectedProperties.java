package org.egov.pt.models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxCollectedProperties {
	private String transactionId;
	private BigDecimal amount;
	private String propertyId;
	private String tenantId;
	
}
