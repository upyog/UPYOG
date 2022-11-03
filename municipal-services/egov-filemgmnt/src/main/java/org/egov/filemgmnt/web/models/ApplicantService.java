package org.egov.filemgmnt.web.models;

import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "A Object holds the  data for a Applicant Service")
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ApplicantService {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Size(max = 64)
    @JsonProperty("applicantPersonalId")
    private String applicantPersonalId;

    @Size(max = 64)
    @JsonProperty("serviceId")
    private String serviceId;

    @Size(max = 64)
    @JsonProperty("serviceCode")
    private String serviceCode;

    @Size(max = 64)
    @JsonProperty("businessService")
    private String businessService;

    @Size(max = 64)
    @JsonProperty("workflowCode")
    private String workflowCode;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}