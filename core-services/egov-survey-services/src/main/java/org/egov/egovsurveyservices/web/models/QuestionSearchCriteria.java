package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.egovsurveyservices.web.models.enums.Status;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class QuestionSearchCriteria {

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("questionStatement")
    private String questionStatement;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("categoryId")
    private String categoryId;

    @JsonProperty("size")
    private Integer size=10;

    @JsonProperty("pageNumber")
    private Integer pageNumber=1;

}
