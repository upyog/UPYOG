package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerRequestNew {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("User")
    User user;

    @JsonProperty("SurveyResponse")
    private SurveyResponseNew surveyResponse;
}
