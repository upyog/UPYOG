package org.egov.pt.models;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PtTaxCalculatorTrackerSearchCriteria {

	private String tenantId;

	private Set<String> tenantIds;

	private Set<String> uuids;

	private Set<String> propertyIds;

	private Set<String> financialYears;
	
	private Integer limit;

}
