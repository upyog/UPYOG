package org.egov.pt.web.contracts;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.models.DashboardReport;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardReportResponse {

	@JsonProperty("ResponseInfo")
	  private ResponseInfo responseInfo;

	  @JsonProperty("Report")
	  private DashboardReport dashboardReport;
}
