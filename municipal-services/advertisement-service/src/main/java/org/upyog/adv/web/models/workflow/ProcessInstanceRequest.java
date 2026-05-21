package org.upyog.adv.web.models.workflow;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
