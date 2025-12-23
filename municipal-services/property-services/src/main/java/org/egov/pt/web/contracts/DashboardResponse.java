package org.egov.pt.web.contracts;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.models.DashboardData;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {

	@JsonProperty("ResponseInfo")
	  private ResponseInfo responseInfo;

	  @JsonProperty("Data")
	  private List<DashboardData> dashboardDatas;
}
