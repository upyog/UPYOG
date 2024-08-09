package org.egov.pt.calculator.web.models;

import java.util.Set;

import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class BillSearchCriteria {
	
	private String tenantId;
	
	private Set<String> consumerCode;
	
	@Size(max = 256)
	private String service;
	
	private String demandId;

}
