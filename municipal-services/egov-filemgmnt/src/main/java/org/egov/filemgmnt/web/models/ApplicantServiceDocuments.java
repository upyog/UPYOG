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

@Schema(description = "A Object holds the  data for a Service Document Details")
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicantServiceDocuments {

    @Schema(type = "string", format = "uuid", description = "Applicant service documents id")
    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Schema(type = "string", format = "uuid", description = "Applicant id")
    @Size(max = 64)
    @JsonProperty("applicantPersonalId")
    private String applicantPersonalId;

    @Schema(type = "string", description = "Service details id")
    @Size(max = 64)
    @JsonProperty("serviceDetailsId")
    private String serviceDetailsId;

    @Schema(type = "string", description = "Document Type id")
    @Size(max = 64)
//    @NotNull
    @JsonProperty("documentTypeId")
    private String documentTypeId;

    @Schema(type = "string", description = "File store id")
    @Size(max = 64)
//    @NotNull
    @JsonProperty("fileStoreId")
    private String fileStoreId;

    @Schema(type = "string", description = "Document active or not")
    @Size(max = 64)
    @JsonProperty("active")
    private String active;

    @Schema(type = "string", description = "Document number")
    @Size(max = 64)
//    @NotNull
    @JsonProperty("documentNumber")
    private String documentNumber;

    @Schema(type = "string", description = "Application Details")
    @Size(max = 64)
    @JsonProperty("applicationDetails")
    private String applicationdetails;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
