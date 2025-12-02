package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ScorecardSurveyRequest {

    @JsonProperty("SurveyEntity")
    ScorecardSurveyEntity surveyEntity;

    @JsonProperty("RequestInfo")
    RequestInfo requestInfo;

}
