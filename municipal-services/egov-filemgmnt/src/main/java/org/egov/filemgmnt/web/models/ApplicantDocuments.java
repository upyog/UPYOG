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

@Schema(description = "A Object holds the basic data for a Applicant Documents")
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ApplicantDocuments {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Size(max = 64)
    @JsonProperty("applicantPersonalId")
    private String applicantPersonalId;

    @Size(max = 64)
    @JsonProperty("documenttypeId")
    private String documenttypeId;

    @Size(max = 64)
    @JsonProperty("documentNumber")
    private String documentNumber;

    @JsonProperty("docexpiryDate")
    private Long docexpiryDate;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
