package org.egov.advertisementcanopy.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteBookingSearchCriteria {

	private List<String> applicationNumbers;

	private List<String> uuids;

	private List<String> createdBy;

	private List<String> status;
	
	private String tenantId;

}
