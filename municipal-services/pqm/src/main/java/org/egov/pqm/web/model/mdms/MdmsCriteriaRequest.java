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
public class MdmsCriteriaRequest {
  @JsonProperty("RequestInfo")
  @Valid
  private RequestInfo requestInfo;

  @JsonProperty("MdmsCriteria")
  @Valid
  private MdmsCriteria mdmsCriteria;
}