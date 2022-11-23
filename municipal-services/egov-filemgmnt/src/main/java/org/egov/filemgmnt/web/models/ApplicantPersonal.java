package org.egov.filemgmnt.web.models;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
    @Size(max = 12)
    @NotNull
//    @Pattern(regexp = "^[1-9][0-9]{11}$")
    @JsonProperty("aadhaarNo")
    private String aadhaarNo;

    @Schema(type = "string", format = "email", description = "Email address")
    @Size(max = 64)
    @Email
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
    @Pattern(regexp = "^[1-9][0-9]{9,14}$")
    @JsonProperty("mobileNo")
    private String mobileNo;

    @Schema(type = "string", description = "Tenant identification number")
    @Size(max = 64)
    @NotNull
    @JsonProperty("tenantId")
    private String tenantId;

    @Schema(type = "string", description = "Father First Name")
    @Size(max = 64)
    @JsonProperty("fatherFirstName")
    private String fatherFirstName;

    @Schema(type = "string", description = "Father Last Name")
    @Size(max = 64)
    @JsonProperty("fatherLastName")
    private String fatherLastName;

    @Schema(type = "string", description = "Mother First Name")
    @Size(max = 64)
    @JsonProperty("motherFirstName")
    private String motherFirstName;

    @Schema(type = "string", description = "Mother Last Name")
    @Size(max = 64)
    @JsonProperty("motherLastName")
    private String motherLastName;

    @Schema(type = "string", description = "Category of Applicant")
    @Size(max = 64)
    @JsonProperty("applicantCategory")
    private String applicantCategory;

    @Schema(type = "string", description = "Date of Birth")
    @Size(max = 64)
    @JsonProperty("dateOfBirth")
    private String dateOfBirth;

    @Schema(type = "string", description = "Bank Account Number")
    @Size(max = 64)
    @JsonProperty("bankAccountNo")
    private String bankAccountNo;

    @Valid
    @NotNull
    @JsonProperty("serviceDetails")
    private ServiceDetails serviceDetails;

    @Valid
    @NotNull
    @JsonProperty("applicantAddress")
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
