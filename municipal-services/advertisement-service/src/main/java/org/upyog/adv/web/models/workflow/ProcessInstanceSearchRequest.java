package org.upyog.adv.web.models.workflow;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for the egov-workflow-v2 <code>/egov-wf/process/_search</code>
 * endpoint. Mirrors the {@code businessIds} query filter. 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceSearchRequest {

  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo;

  @JsonProperty("businessIds")
  private List<String> businessIds;
}
