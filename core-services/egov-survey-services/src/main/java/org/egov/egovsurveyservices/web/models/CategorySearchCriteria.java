package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CategorySearchCriteria {

    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("label")
    private String label;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("size")
    private Integer size=10;

    @JsonProperty("pageNumber")
    private Integer pageNumber=1;

}
