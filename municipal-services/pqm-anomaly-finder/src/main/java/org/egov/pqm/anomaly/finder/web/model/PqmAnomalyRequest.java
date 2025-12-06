package org.egov.pqm.anomaly.finder.web.model;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PqmAnomalyRequest {

	  @JsonProperty("RequestInfo")
	  private RequestInfo requestInfo = null;

	  @JsonProperty("pqmAnomalys")
	  private List<PqmAnomaly> pqmAnomalys = null;
}
