package org.egov.egovsurveyservices.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Answer {

    @JsonProperty("answerUuid")
    private String answerUuid;

    @JsonProperty("city")
    private String city;

    @JsonProperty("surveyUuid")
    private String surveyUuid;

    @JsonProperty("sectionUuid")
    private String sectionUuid;

    @JsonProperty("questionUuid")
    private String questionUuid;

    @JsonProperty("questionStatement")
    private String questionStatement;

    @JsonProperty("comments")
    private String comments;

    @JsonProperty("answer")
    private List<Object> answer;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("citizenId")
    private String citizenId;

}
