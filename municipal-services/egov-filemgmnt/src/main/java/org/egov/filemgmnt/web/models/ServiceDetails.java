package org.egov.filemgmnt.web.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "A Object holds the  data for a Service Details")
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ServiceDetails {

    @Schema(type = "string", format = "uuid", description = "Service details id")
    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Schema(type = "string", description = "Applicant id")
    @Size(max = 64)
    @JsonProperty("applicantPersonalId")
    private String applicantPersonalId;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @Schema(type = "string", description = "Service id")
    @Size(max = 64)
    @JsonProperty("serviceId")
    private String serviceId;

    @Schema(type = "string", description = "Service code")
    @Size(max = 64)
    @NotBlank(message = "Service code is required")
    @JsonProperty("serviceCode")
    private String serviceCode;

    @Schema(type = "string", description = "Service Sub Type")
    @Size(max = 64)
    @NotBlank(message = "Service Sub Type is required")
    @JsonProperty("serviceSubType")
    private String serviceSubType;

    @Schema(type = "string", description = "Service Minor Type")
    @Size(max = 64)
    @NotBlank(message = "Service Minor Type is required")
    @JsonProperty("serviceMinorType")
    private String serviceMinorType;
}
