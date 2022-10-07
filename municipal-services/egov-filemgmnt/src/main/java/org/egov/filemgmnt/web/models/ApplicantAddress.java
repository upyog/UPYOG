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

@Schema(description = "A Object holds the basic data for a Applicant Address")
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicantAddress {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Size(max = 64)
    @JsonProperty("houseNo")
    private String houseNo;

    @Size(max = 64)
    @JsonProperty("houseName")
    private String houseName;

    @Size(max = 64)
    @JsonProperty("street")
    private String street;

    @Size(max = 64)
    @JsonProperty("pincode")
    private String pincode;

    @Size(max = 64)
    @JsonProperty("postOfficeName")
    private String postOfficeName;

    @Size(max = 64)
    @JsonProperty("applicantPersonalId")
    private String applicantPersonalId;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}