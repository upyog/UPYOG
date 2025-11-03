package org.egov.asset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AssetSearchDTO implements AssetDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("assetBookRefNo")
    private String assetBookRefNo;

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

    @JsonProperty("department")
    private String department;

    @JsonProperty("applicationNo")
    private String applicationNo;

    @JsonProperty("applicationDate")
    private Long applicationDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("location")
    private String location;

    @JsonProperty("division")
    private String division;

    @JsonProperty("district")
    private String district;

    @JsonProperty("disposedReason")
    private String disposedReason;

    @JsonProperty("acquisitionCost")
    private Long acquisitionCost;

    @JsonProperty("acquisitionDate")
    private Long acquisitionDate;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("unitOfMeasurement")
    private Long unitOfMeasurement;

}
