package org.upyog.adv.web.models.billing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetBillCriteria {
	
	private String applicationNumber;
	
	@Default
	private BigDecimal amountExpected = BigDecimal.ZERO;
	
	private String assessmentYear;
	
	@NotNull
	private String tenantId;
	
	private String billId;

	@NotNull
	private List<String> consumerCodes;

	private Long fromDate;

	private Long toDate;
	
}