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

@Schema(description = "A Object holds the basic data for a Applicant Personal")
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicantPersonal {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Size(max = 64)
    @JsonProperty("aadhaarNo")
    private String aadhaarNo;

    @Size(max = 64)
    @JsonProperty("email")
    private String email;

    @Size(max = 64)
    @JsonProperty("firstName")
    private String firstName;

    @Size(max = 64)
    @JsonProperty("lastName")
    private String lastName;

    @Size(max = 64)
    @JsonProperty("title")
    private String title;

    @Size(max = 15)
    @JsonProperty("mobileNo")
    private String mobileNo;

    @Size(max = 64)
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
