package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ScorecardQuestionResponse {

    @NotNull
    @JsonProperty("questionUuid")
    private String questionUuid;

    @JsonProperty("questionStatement")
    private String questionStatement;

//    @JsonProperty("answer")
//    private List<Object> answer;

    @JsonProperty("answerResponse")
    private AnswerNew answerResponse;

    @JsonProperty("answerUuid")
    private String answerUuid;

    @JsonProperty("comments")
    private String comments;
}