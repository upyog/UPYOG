package org.egov.advertisementcanopy.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteSearchCriteria {
	private List<String> siteId;

	private List<String> uuids;

	private List<String> createdBy;
	
	private String tenantId;

}
