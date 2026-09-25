package org.egov.echallan.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceRequest {

  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo;

  @JsonProperty("ProcessInstances")
  private List<ProcessInstance> processInstances;
}
