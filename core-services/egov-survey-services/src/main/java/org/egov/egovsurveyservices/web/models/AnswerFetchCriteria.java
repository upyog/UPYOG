package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AnswerFetchCriteria {

    @NotBlank
    @JsonProperty("surveyUuid")
    private String surveyUuid;

    @NotBlank
    @JsonProperty("citizenId")
    private String citizenId;

    @NotBlank
    @JsonProperty("tenantId")
    private String tenantId;

}
