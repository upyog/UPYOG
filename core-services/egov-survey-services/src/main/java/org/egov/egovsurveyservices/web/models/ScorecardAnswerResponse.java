package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScorecardAnswerResponse {

	  @JsonProperty("ResponseInfo")
	    private ResponseInfo responseInfo;  

    @NotNull
    @JsonProperty("surveyUuid")
    private String surveyUuid;

    @NotNull
    @JsonProperty("citizenId")
    private String citizenId;

    @NotNull
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("locality")
    private String locality;

    @JsonProperty("status")
    private String status;

    @JsonProperty("coordinates")
    private String coordinates;

    @JsonProperty("sectionResponses")
    private List<ScorecardSectionResponse> sectionResponses;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}