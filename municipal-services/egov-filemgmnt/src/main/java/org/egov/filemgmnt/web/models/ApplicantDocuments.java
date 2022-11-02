package org.egov.filemgmnt.web.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "A Object holds the basic data for a Applicant Documents")
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ApplicantDocuments {

    @Schema(type = "string", format = "uuid", description = "Applicant Document id")
    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Schema(type = "string", format = "uuid", description = "Applicant id")
    @Size(max = 64)
    @JsonProperty("applicantPersonalId")
    private String applicantPersonalId;

    @Schema(type = "string", description = "Document type id")
    @Size(max = 64)
    @NotNull
    @JsonProperty("documenttypeId")
    private String documenttypeId;

    @Schema(type = "string", description = "Document number")
    @Size(max = 64)
    @NotNull
    @JsonProperty("documentNumber")
    private String documentNumber;

    @Schema(type = "long", description = "Document expiry date")
    @NotNull
    @JsonProperty("docexpiryDate")
    private Long docexpiryDate;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
