package org.egov.pqm.anomaly.finder.web.model;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Response of the PqmAnomaly
 */
@Validated
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PqmAnomalyResponse {
	
	  @JsonProperty("totalCount")
	  private Integer totalCount =0;
	  
	  @JsonProperty("ResponseInfo")
	  private ResponseInfo responseInfo = null;

	  @JsonProperty("pqmAnomalys")
	  private List<PqmAnomaly> pqmAnomalys = null;

}
