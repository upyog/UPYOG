package org.egov.pt.models.collection;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RevenuDataBucket {
	
	private String propertyId;
	private String tenantId;
	private BigDecimal penaltyamount;
	private BigDecimal totalBillAmount;
	private String transactionNumber;
	private Long creationTime; 
	
}
