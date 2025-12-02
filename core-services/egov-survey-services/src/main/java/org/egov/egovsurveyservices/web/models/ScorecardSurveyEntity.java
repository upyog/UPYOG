package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ScorecardSurveyEntity {
    @JsonProperty("uuid")
    private String uuid;
    
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("surveyTitle")
    private String surveyTitle;

    @JsonProperty("surveyCategory")
    private String surveyCategory;

    @JsonProperty("surveyDescription")
    private String surveyDescription;

    @NotNull
    @JsonProperty("sections")
    private List<Section> sections;

    @NotNull(message = "Start date is required")
    @JsonProperty("startDate")
    private Long startDate;

    @NotNull(message = "End date is required")
    @JsonProperty("endDate")
    private Long endDate;

    @JsonProperty("postedBy")
    private String postedBy;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("answersCount")
    private Long answersCount;

    @JsonProperty("hasResponded")
    private Boolean hasResponded;
    
    @JsonProperty("createdTime")
    private Long createdTime;
    
    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;

    public ScorecardSurveyEntity addSectionsItem(Section sectionItem) {
        if (this.sections == null) {
            this.sections = new ArrayList<>();
        }
        if (null != sectionItem) this.sections.add(sectionItem);
        return this;
    }

}