package org.egov.asset.web.models;

import java.util.List;

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
	
        //@JsonProperty("applicant")
        //private Applicant applicant = null;

		@JsonProperty("id")
        private String id = null;

        @JsonProperty("tenantId")
        private String tenantId = null;
        
        @JsonProperty("assetId")
        private String assetId;

        @JsonProperty("assetBookRefNo")
        private String assetBookRefNo = null;

        @JsonProperty("assetName")
        private String assetName = null;

        @JsonProperty("description")
        private String description = null;

        @JsonProperty("assetClassification")
        private String assetClassification = null;
        
        @JsonProperty("assetParentCategory")
        private String assetParentCategory = null;

        @JsonProperty("assetCategory")
        private String assetCategory = null;
        
        @JsonProperty("assetSubCategory")
        private String assetSubCategory = null;
        
        @JsonProperty("department")
        private String department = null;

        @JsonProperty("applicationNo")
        private String applicationNo = null;

        @JsonProperty("approvalNo")
        private String approvalNo = null;

        @JsonProperty("approvalDate")
        private Long approvalDate = null;

        @JsonProperty("applicationDate")
        private Long applicationDate = null;

        @JsonProperty("status")
        private String status = null;

//        @JsonProperty("action")
//        private String action = null;
//
//        @JsonProperty("businessService")
//        private String businessService = null;

        @JsonProperty("addressDetails")
        private Address addressDetails = null;
        
        @JsonProperty("documents")
        @Valid
        private List<Document> documents = null;

        @JsonIgnore
        @JsonProperty("auditDetails")
        private AuditDetails auditDetails = null;

        @JsonProperty("additionalDetails")
        private Object additionalDetails = null;
        
        @JsonProperty("accountId")
        private String accountId = null;
        
        @JsonProperty("assetCurrentUsage")
        private String assetCurrentUsage = null;
        
        @JsonProperty("remarks")
        private String remarks = null;
        
        @JsonProperty("workflow")
    	private ProcessInstance workflow = null;

//        @JsonProperty("parentAssetSpecificDetails")
//        private AssetParentAssetSpecificDetails parentAssetSpecificDetails = null;
        
        public Asset(String type, Object additionalInformation) {
            this.assetParentCategory = type;
            this.additionalDetails = additionalInformation;
        }


}

