package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.egov.asset.dto.AssetDTO;
import org.egov.asset.web.models.workflow.ProcessInstance;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Transient;
import javax.validation.Valid;
import java.util.List;

/**
 * An object representing an asset
 */
@ApiModel(description = "Object representing an asset")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asset implements AssetDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;



    @JsonProperty("assetName")
    private String assetName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("assetClassification")
    private String assetClassification;

    @JsonProperty("assetParentCategory")
    private String assetParentCategory;

    @JsonProperty("assetCategory")
    private String assetCategory;

    @JsonProperty("assetSubCategory")
    private String assetSubCategory;

    @JsonProperty("applicationNo")
    private String applicationNo;

    @JsonProperty("approvalNo")
    private String approvalNo;

    @JsonProperty("approvalDate")
    private Long approvalDate;

    @JsonProperty("applicationDate")
    private Long applicationDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("addressDetails")
    private Address addressDetails;

    @JsonProperty("documents")
    @Valid
    private List<Document> documents;

    @JsonIgnore
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("assetAssignment")
    private AssetAssignment assetAssignment;

    @JsonProperty("assetInventory")
    private AssetInventory assetInventory;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("workflow")
    private ProcessInstance workflow;

    @JsonProperty("purchaseDate")
    private Long purchaseDate;

    @JsonProperty("bookRefNo")
    private String bookRefNo;

    @JsonProperty("unitOfMeasurement")
    private Long unitOfMeasurement;

    @JsonProperty("assetType")
    private String assetType;

    @JsonProperty("acquisitionCost")
    private double acquisitionCost;

    @JsonProperty("minimumValue")
    private String minimumValue;

    @JsonProperty("islegacyData")
    private String islegacyData;

    @JsonProperty("division")
    private String division;

    @JsonProperty("district")
    private String district;

    @Transient
    private String accumulatedDepreciartion;

//      @JsonProperty("parentAssetSpecificDetails")
//      private AssetParentAssetSpecificDetails parentAssetSpecificDetails = null;

    public Asset(String type, Object additionalInformation) {
        this.assetParentCategory = type;
        this.additionalDetails = additionalInformation;
    }


}