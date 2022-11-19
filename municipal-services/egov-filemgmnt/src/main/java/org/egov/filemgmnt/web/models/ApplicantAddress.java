package org.egov.filemgmnt.web.models;

import javax.validation.constraints.NotBlank;
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

@Schema(description = "A Object holds the basic data for a Applicant Address")
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicantAddress {

    @Schema(type = "string", format = "uuid", description = "Applicant Address id")
    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Schema(type = "string", description = "House number")
    @Size(max = 64)
    @NotBlank(message = "House number is requried")
    @JsonProperty("houseNo")
    private String houseNo;

    @Schema(type = "string", description = "House name")
    @Size(max = 64)
    @NotBlank(message = "House name is requried")
    @JsonProperty("houseName")
    private String houseName;

    @Schema(type = "string", description = "Street name")
    @Size(max = 64)
    @JsonProperty("street")
    private String street;

    @Schema(type = "string", description = "Pincode")
    @Size(max = 64)
    @NotNull
    @JsonProperty("pincode")
    private String pincode;

    @Schema(type = "string", description = "Postoffice Name")
    @Size(max = 64)
    @JsonProperty("postOfficeName")
    private String postOfficeName;

    @Schema(type = "string", description = "Residence Association No")
    @Size(max = 64)
    @JsonProperty("residenceAssociationNo")
    private String residenceassociationno;

    @Schema(type = "string", description = "Local Place")
    @Size(max = 64)
    @JsonProperty("localPlace")
    private String localplace;

    @Schema(type = "string", description = "Main Place")
    @Size(max = 64)
    @JsonProperty("mainPlace")
    private String mainplace;

    @Schema(type = "string", description = "Ward No")
    @Size(max = 64)
    @JsonProperty("wardNo")
    private String wardno;

    @Schema(type = "string", format = "uuid", description = "Applicant id")
    @Size(max = 64)
    @JsonProperty("applicantPersonalId")
    private String applicantPersonalId;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}