package org.egov.demand.model;

import lombok.Data;

@Data
public class TaxHeadMasterFinance {
	
	private String billingservicecode;
	private String taxhead;
	private String glcode;
	private Long validFrom;
	private Long validTo;
	private String serviceAttribute;

}
