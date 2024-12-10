package org.egov.asset.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

		public boolean isEmpty() {
			return (this.tenantId == null && this.status == null && this.ids == null && this.applicationNo == null
	                && this.approvalNo == null && this.approvalDate == null && this.assetClassification == null && this.assetParentCategory == null
	                && this.approvalDate == null && this.assetBookRefNo == null && this.createdBy == null);
		}
		
		public boolean tenantIdOnly() {
			return (this.tenantId != null && this.status == null && this.ids == null && this.applicationNo == null
	                && this.approvalNo == null && this.approvalDate == null && this.assetClassification == null && this.assetParentCategory == null
	                && this.approvalDate == null && this.assetBookRefNo == null && this.createdBy == null);
	    }

}
