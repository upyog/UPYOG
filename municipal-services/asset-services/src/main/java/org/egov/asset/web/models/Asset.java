package org.egov.asset.web.models;

import java.util.List;

import javax.persistence.Transient;
import javax.validation.Valid;

import org.egov.asset.web.models.workflow.ProcessInstance;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An object representing an asset
 */
@ApiModel(description = "An object representing an asset")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asset   {

		@JsonProperty("id")
        private String id ;

        @JsonProperty("tenantId")
        private String tenantId;

        @JsonProperty("assetBookRefNo")
        private String assetBookRefNo ;

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

        @JsonProperty("additionalDetails")
        private Object additionalDetails;
        
        @JsonProperty("accountId")
        private String accountId;
        
        @JsonProperty("assetCurrentUsage")
        private String assetCurrentUsage ;
        
        @JsonProperty("remarks")
        private String remarks;
        
        @JsonProperty("financialYear")
        private String financialYear;
        
        @JsonProperty("sourceOfFinance")
        private String sourceOfFinance ;
        
        @JsonProperty("workflow")
    	private ProcessInstance workflow;
        
        @JsonProperty("scheme")
        private String scheme;

        @JsonProperty("subScheme")
        private String subScheme;
        
        @JsonProperty("orderNumber")
        private String orderNumber;
        
        @JsonProperty("orderDate")
        private Long orderDate;
        
        @JsonProperty("assetAssignment")
    	private AssetAssignment assetAssignment;
        
        @JsonProperty("bookValue")
        private float bookValue;
        
        @JsonProperty("orignalCost:")
        private float orignalCost;
        
        @Transient
        private float totalCost;
        
        @Transient
        @JsonProperty("grossValue:")
        private float grossValue;
        
        @JsonProperty("purchaseAcquisitionDate")
        private Long acquisitiondate;
        
        @JsonProperty("depreciationRate")
        private float depreciationRate;
        
        @JsonProperty("currentValue")
        private float currentValue;
        
        @JsonProperty("revenueGeneratedByAsset")
        private float revenueGeneratedByAsset;
        
        @JsonProperty("lastMaintenanceDate")
        private Long lastMaintenanceDate;
        
        @JsonProperty("estimatedNextMaintenanceDate")
        private Long estimatedNextMaintenanceDate;
        
        @JsonProperty("code")
        private Long code;
        
        @JsonProperty("oldCode")
        private Long oldCode;
        
        
        @JsonProperty("unitOfMeasurement")
        private Long unitOfMeasurement;
        
        
        @JsonProperty("warrantyYears")
        private Long warrantyYears;
        
        @JsonProperty("warrantyExpiryDate")
        private Long warrantyExpiryDate;
       
        
        
        
        
//      @JsonProperty("parentAssetSpecificDetails")
//      private AssetParentAssetSpecificDetails parentAssetSpecificDetails = null;
        
        public Asset(String type, Object additionalInformation) {
            this.assetParentCategory = type;
            this.additionalDetails = additionalInformation;
        }


}

