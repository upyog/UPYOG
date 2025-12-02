package org.egov.egovsurveyservices.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.egovsurveyservices.web.models.enums.Status;
import org.egov.egovsurveyservices.web.models.enums.Type;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Question {

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("tenantId")
    @NotBlank
    private String tenantId;

    @JsonProperty("surveyId")
    private String surveyId;

    @NotBlank(message = "Question statement cannot be blank")
    @JsonProperty("questionStatement")
    private String questionStatement;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("type")
    @NotNull(message="The value provided is either Invalid or null")
    private Type type;

    @JsonIgnore
    private Boolean required;

    @JsonProperty("qorder")
    private Long qorder;

    @JsonProperty("categoryId")
    @NotBlank
    private String categoryId;

    @JsonProperty("category")
    private Category category;

    @JsonProperty("options")
    private List<QuestionOption> options;

}
