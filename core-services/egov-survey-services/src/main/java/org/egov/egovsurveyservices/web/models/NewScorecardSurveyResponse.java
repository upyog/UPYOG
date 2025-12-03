package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewScorecardSurveyResponse {

    private String uuid;
    private String surveyUuid;
    private String citizenId;
    private String tenantId;
    private String locality;
    private String status;
    private AuditDetails auditDetails;
    private Long createdTime;
    private Long lastModifiedTime;
}
