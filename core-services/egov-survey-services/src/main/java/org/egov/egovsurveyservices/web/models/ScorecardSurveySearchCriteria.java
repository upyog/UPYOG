package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ScorecardSurveySearchCriteria {

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("title")
    private String title;
    
    @JsonProperty("active")
    private Boolean active;
    
    @JsonProperty("openSurveyFlag")
    private Boolean openSurveyFlag;
    
    @JsonIgnore
    private Boolean isCountCall = false;

}
