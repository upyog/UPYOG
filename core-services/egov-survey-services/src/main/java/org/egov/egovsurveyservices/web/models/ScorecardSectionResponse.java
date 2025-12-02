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
public class ScorecardSectionResponse {

    @NotNull
    @JsonProperty("sectionUuid")
    private String sectionUuid;

    @JsonProperty("questionResponses")
    private List<ScorecardQuestionResponse> questionResponses;
}