package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AnswerEntity {

    @JsonProperty("surveyId")
    private String surveyId;

    @JsonProperty("city")
    private String city;

    @JsonProperty("answers")
    private List<Answer> answers;
}
