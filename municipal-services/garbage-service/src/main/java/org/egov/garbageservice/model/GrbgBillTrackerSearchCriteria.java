package org.egov.garbageservice.model;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrbgBillTrackerSearchCriteria {

	private String tenantId;
	
	private String type;
	
	private String month;
	
	private Set<String> status;

	private Set<String> tenantIds;

	private Set<String> uuids;

	private Set<String> grbgApplicationIds;

	private Set<String> billIds;
	
	private Integer limit;


}
