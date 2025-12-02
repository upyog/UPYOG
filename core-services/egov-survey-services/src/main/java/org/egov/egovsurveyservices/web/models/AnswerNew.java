package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerNew {
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("sectionUuid")
    private String sectionUuid;
    @JsonProperty("sectionWeightage")
    private BigDecimal sectionWeightage;
    @JsonProperty("questionUuid")
    private String questionUuid;
    @JsonProperty("questionStatement")
    private String questionStatement;
    @JsonProperty("questionWeightage")
    private BigDecimal questionWeightage;
    @JsonProperty("answerDetails")
    private List<AnswerDetail> answerDetails;
    @JsonProperty("comments")
    private String comments;
    private AuditDetails auditDetails;
}
