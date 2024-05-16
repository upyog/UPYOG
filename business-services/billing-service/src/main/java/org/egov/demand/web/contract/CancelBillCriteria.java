package org.egov.demand.web.contract;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CancelBillCriteria {
	
	@NotNull
	private String tenantId;
	
	@NotNull
	private String businessService;
	
	@NotNull
	private String consumerCode;

}
