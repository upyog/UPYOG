package org.egov.asset.web.models.maintenance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.asset.web.models.AuditDetails;
import org.egov.asset.web.models.Document;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@ApiModel(description = "Object representing an asset maintenance details")
@Validated

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetMaintenance {

    @Id
    @JsonProperty("maintenanceId")
    private String maintenanceId;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("assetId")
    private String assetId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("currentLifeOfAsset")
    private String currentLifeOfAsset;

    @JsonProperty("isWarrantyExpired")
    private Boolean isWarrantyExpired;

    @JsonProperty("isAMCExpired")
    private Boolean isAMCExpired;

    // Warranty status: IN_WARRANTY, IN_AMC, NA
    @JsonProperty("warrantyStatus")
    private String warrantyStatus;

    // AMC details, if applicable
    @JsonProperty("amcDetails")
    private String amcDetails;

    // Maintenance type: Preventive or Corrective
    @JsonProperty("maintenanceType")
    private String maintenanceType;

    // Payment type: Warranty, AMC, To be Paid
    @JsonProperty("paymentType")
    private String paymentType;

    @JsonProperty("costOfMaintenance")
    private Double costOfMaintenance;

    // Vendor responsible for maintenance
    @JsonProperty("vendor")
    private String vendor;

    // Maintenance cycle: Monthly, Quarterly, etc.
    @JsonProperty("maintenanceCycle")
    private String maintenanceCycle;

    // Parts added or replaced during maintenance
    @JsonProperty("partsAddedOrReplaced")
    private String partsAddedOrReplaced;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    // Remarks after maintenance
    @JsonProperty("postConditionRemarks")
    private String postConditionRemarks;

    // Remarks before maintenance
    @JsonProperty("preConditionRemarks")
    private String preConditionRemarks;

    // Remarks before maintenance
    @JsonProperty("assetMaintenanceStatus")
    private String assetMaintenanceStatus;

    // Attached documents for maintenance
    @JsonProperty("documents")
    @Valid
    private List<Document> documents;

    @JsonIgnore
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    // Detailed description of maintenance
    @Column(length = 5000)
    @JsonProperty("description")
    private String description;

    @JsonProperty("assetMaintenanceDate")
    private Long assetMaintenanceDate;

    @JsonProperty("assetNextMaintenanceDate")
    private Long assetNextMaintenanceDate;

}
