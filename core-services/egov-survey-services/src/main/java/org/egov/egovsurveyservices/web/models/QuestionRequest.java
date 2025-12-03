package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class QuestionRequest {

    @JsonProperty("RequestInfo")
    RequestInfo requestInfo;

    @JsonProperty("Questions")
    @Valid
    List<Question> questions;

}
