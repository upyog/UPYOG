package org.egov.pt.web.contracts;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDataRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	@JsonProperty("Data")
	private List<Data> datas;

}
