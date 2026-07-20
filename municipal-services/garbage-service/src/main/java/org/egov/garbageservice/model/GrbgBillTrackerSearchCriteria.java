package org.egov.garbageservice.model;

import java.util.List;
import java.util.Set;

import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
/**
 * Search filters for bill tracker records (tenant, application, period, status, bill id).
 * Nested in GrbgBillTrackerRequest for repository and service queries.
 */
@AllArgsConstructor
public class GrbgBillTrackerSearchCriteria {

	@CustomSafeHtml
	private String tenantId;
	
	private Set<String> type;
	
	@CustomSafeHtml
	private String month;
	
	@CustomSafeHtml
	private String year;
	
	private Set<String> status;

	private Set<String> tenantIds;

	private Set<String> uuids;

	private Set<String> grbgApplicationIds;

	private Set<String> billIds;
	
	private Set<String> demandIds;
	
	private Integer limit;


}
