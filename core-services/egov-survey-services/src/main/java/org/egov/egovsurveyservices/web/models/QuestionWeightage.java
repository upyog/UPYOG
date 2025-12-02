package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class QuestionWeightage {
    @JsonProperty("questionUuid")
    private String questionUuid;
    
    @JsonProperty("sectionUuid")
    private String sectionUuid;
    
    @JsonProperty("qorder")
    private Long qorder;

    @JsonProperty("required")
    private Boolean required;
    
    @JsonProperty("question")
    private Question question;

    @NotNull
    @JsonProperty("weightage")
    private BigDecimal weightage;
    
    public QuestionWeightage(String questionUuid, Question question, BigDecimal weightage) {
        this.questionUuid = questionUuid;
        this.question = question;
        this.weightage = weightage;
    }
}