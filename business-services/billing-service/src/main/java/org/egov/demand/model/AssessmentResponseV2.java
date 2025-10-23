package org.egov.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;


import java.util.List;

/**
 * The response of create or update of assessment. Contains the ResponseHeader and created/updated assessment
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResponseV2 {
  @JsonProperty("ResponseInfo")
  private ResponseInfo responseInfo;

  @JsonProperty("Assessments")
  private List<AssessmentV2> assessments;

 
}
