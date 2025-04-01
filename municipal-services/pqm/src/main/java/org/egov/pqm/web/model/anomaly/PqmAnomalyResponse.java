package org.egov.pqm.web.model.anomaly;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Response of the PqmAnomaly
 */
@Validated
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PqmAnomalyResponse {
	
	  @JsonProperty("totalCount")
	  private Integer totalCount =0;
	  
	  @JsonProperty("ResponseInfo")
	  private ResponseInfo responseInfo = null;

	  @JsonProperty("pqmAnomalys")
	  private List<PqmAnomaly> pqmAnomalys = null;

}
