package org.egov.pqm.web.model.idgen;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.egov.common.contract.request.RequestInfo;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class IdGenerationRequest {

  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo;

  private List<IdRequest> idRequests;


}
