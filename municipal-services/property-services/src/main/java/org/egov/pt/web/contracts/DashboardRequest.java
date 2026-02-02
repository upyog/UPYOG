package org.egov.pt.web.contracts;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.DashboardDataSearch;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	@JsonProperty("DashboardFilters")
	private DashboardDataSearch dashboardDataSearch;

}
