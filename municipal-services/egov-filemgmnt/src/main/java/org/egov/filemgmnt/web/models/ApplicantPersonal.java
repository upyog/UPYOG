package org.egov.filemgmnt.web.models;

import javax.validation.Valid;
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

@Schema(description = "Applicant details")
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicantPersonal {

    @Schema(type = "string", format = "uuid", description = "Applicant id")
    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Schema(type = "string", description = "Aadhaar number")
    @Size(max = 64)
    @NotNull
    @JsonProperty("aadhaarNo")
    private String aadhaarNo;

    @Schema(type = "string", format = "email", description = "Email address")
    @Size(max = 64)
    @JsonProperty("email")
    private String email;

    @Schema(type = "string", description = "First name")
    @Size(max = 64)
    @NotNull
    @JsonProperty("firstName")
    private String firstName;

    @Schema(type = "string", description = "Last name")
    @Size(max = 64)
    @NotNull
    @JsonProperty("lastName")
    private String lastName;

    @Schema(type = "string", description = "Title")
    @Size(max = 64)
    @JsonProperty("title")
    private String title;

    @Schema(type = "string", description = "Mobile number")
    @Size(max = 15)
    @NotNull
    @JsonProperty("mobileNo")
    private String mobileNo;

    @Schema(type = "string", description = "Tenant identification number")
    @Size(max = 64)
    @NotNull
    @JsonProperty("tenantId")
    private String tenantId;

    @Valid
    @NotNull
    @JsonProperty("serviceDetails")
    private ServiceDetails serviceDetails;

    @Valid
    @NotNull
    @JsonProperty("applicantAddresses")
    private ApplicantAddress applicantAddress;

    @Valid
    @NotNull
    @JsonProperty("applicantServiceDocuments")
    private ApplicantServiceDocuments applicantServiceDocuments;

    @Valid
    @NotNull
    @JsonProperty("applicantDocuments")
    private ApplicantDocuments applicantDocuments;

    @Valid
    @NotNull
    @JsonProperty("fileDetail")
    private FileDetail fileDetail;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
