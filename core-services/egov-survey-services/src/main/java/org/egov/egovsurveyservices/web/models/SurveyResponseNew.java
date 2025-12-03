package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.egov.egovsurveyservices.web.models.enums.SurveyStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyResponseNew {
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("surveyUuid")
    private String surveyUuid;
    @JsonProperty("tenantId")
    private String tenantId="";
    @JsonProperty("city")
    private String city;
    @JsonProperty("citizenId")
    private String citizenId;
    @JsonProperty("locality")
    private String locality = "";
    @JsonProperty("coordinates")
    private String coordinates = "";
    @JsonProperty("status")
    private SurveyStatus status;
    @JsonProperty("answers")
    private List<AnswerNew> answers;
    @JsonProperty("userDetails")
    private Object userDetails;
}
