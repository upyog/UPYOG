package org.egov.pqm.web.model.anomaly;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

import java.util.List;

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
