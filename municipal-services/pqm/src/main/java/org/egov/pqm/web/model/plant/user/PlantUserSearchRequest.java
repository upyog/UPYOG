package org.egov.pqm.web.model.plant.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pqm.web.model.Pagination;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantUserSearchRequest {

  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo;

  @JsonProperty("plantUserSearchCriteria")
  private PlantUserSearchCriteria plantUserSearchCriteria;

  @JsonProperty("pagination")
  private Pagination pagination;
}
