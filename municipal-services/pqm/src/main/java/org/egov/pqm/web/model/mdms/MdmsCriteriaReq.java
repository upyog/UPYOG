package org.egov.pqm.web.model.mdms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;


import javax.validation.Valid;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MdmsCriteriaReq {
  @JsonProperty("RequestInfo")
  @Valid
  private RequestInfo requestInfo = null;

  @JsonProperty("MdmsCriteria")
  @Valid
  private MdmsCriteria mdmsCriteria =null;
}