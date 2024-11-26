package org.egov.rentlease.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCriteria {
	
	private List<String> applicationNo;
	
	private List<String> uuids;

	private List<String> createdBy;

	private List<String> status;
	
	private String tenantId;

}
