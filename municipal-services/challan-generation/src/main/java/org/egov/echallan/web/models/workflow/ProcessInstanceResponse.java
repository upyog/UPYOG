package org.egov.echallan.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessInstanceResponse {
  @JsonProperty("ResponseInfo")
  private ResponseInfo responseInfo;

  @JsonProperty("ProcessInstances")
  private List<ProcessInstance> processInstances;
}
