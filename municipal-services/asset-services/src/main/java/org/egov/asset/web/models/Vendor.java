package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * Vendor details for asset inventory
 */
@ApiModel(description = "Vendor details for asset inventory")
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vendor {

    @JsonProperty("vendorId")
    private String vendorId;

    @JsonProperty("vendorNumber")
    private String vendorNumber;

    @JsonProperty("vendorName")
    private String vendorName;

    @JsonProperty("contactPerson")
    private String contactPerson;

    @JsonProperty("contactNumber")
    private String contactNumber;

    @JsonProperty("contactEmail")
    private String contactEmail;

    @JsonProperty("gstin")
    private String gstin;

    @JsonProperty("pan")
    private String pan;

    @JsonProperty("vendorAddress")
    private String vendorAddress;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("auditDetails")
    @Valid
    private AuditDetails auditDetails;
}