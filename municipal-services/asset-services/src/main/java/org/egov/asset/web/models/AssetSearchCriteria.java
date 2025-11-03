package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetSearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("ids")
    private List<String> ids;

    @JsonProperty("status")
    private String status;

    @JsonProperty("applicationNo")
    private String applicationNo;

    @JsonProperty("approvalNo")
    private String approvalNo;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("approvalDate")
    private Long approvalDate;

    @JsonProperty("fromDate")
    private Long fromDate;

    @JsonProperty("toDate")
    private Long toDate;

    @JsonProperty("businessService")
    @JsonIgnore
    private List<String> businessService;

    @JsonProperty("createdBy")
    @JsonIgnore
    private List<String> createdBy;

    @JsonProperty("locality")
    private String locality;

    @JsonProperty("assetClassification")
    private String assetClassification;

    @JsonProperty("assetParentCategory")
    private String assetParentCategory;

    @JsonProperty("assetBookRefNo")
    private String assetBookRefNo;

    @JsonProperty("acknowledgementIds")
    private List<String> acknowledgementIds;

    // New search parameters
    @JsonProperty("division")
    private String division;

    @JsonProperty("district")
    private String district;

    @JsonProperty("office")
    private String office;

    @JsonProperty("uain")
    private String uain;

    @JsonProperty("disposedReason")
    private String disposedReason;

    @JsonProperty("parentCategory")
    private String parentCategory;

    @JsonProperty("category")
    private String category;

    @JsonProperty("assetName")
    private String assetName;

    @JsonProperty("assetDescription")
    private String assetDescription;

    @JsonProperty("acquisitionCost")
    private Double acquisitionCost;

    @JsonProperty("acquisitionDate")
    private Long acquisitionDate;

    @JsonProperty("minAcquisitionCost")
    private Double minAcquisitionCost;

    @JsonProperty("maxAcquisitionCost")
    private Double maxAcquisitionCost;

    public boolean isEmpty() {
        return (this.tenantId == null && this.status == null && this.ids == null && this.acknowledgementIds == null && this.applicationNo == null
                && this.approvalNo == null && this.approvalDate == null && this.assetClassification == null && this.assetParentCategory == null
                && this.assetBookRefNo == null && this.createdBy == null && this.division == null && this.district == null
                && this.office == null && this.uain == null && this.disposedReason == null && this.parentCategory == null
                && this.category == null && this.assetName == null && this.assetDescription == null && this.acquisitionCost == null
                && this.acquisitionDate == null && this.minAcquisitionCost == null && this.maxAcquisitionCost == null);
    }

    public boolean tenantIdOnly() {
        return (this.tenantId != null && this.status == null && this.ids == null && this.acknowledgementIds == null && this.applicationNo == null
                && this.approvalNo == null && this.approvalDate == null && this.assetClassification == null && this.assetParentCategory == null
                && this.assetBookRefNo == null && this.createdBy == null && this.division == null && this.district == null
                && this.office == null && this.uain == null && this.disposedReason == null && this.parentCategory == null
                && this.category == null && this.assetName == null && this.assetDescription == null && this.acquisitionCost == null
                && this.acquisitionDate == null && this.minAcquisitionCost == null && this.maxAcquisitionCost == null);
    }

}
