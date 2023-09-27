package org.egov.web.models;

import java.sql.Date;
import java.time.LocalDate;

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
public class WMSTenderEntryApplication {
	
	@JsonProperty("tender_id")
	private String tenderId=null;
	@JsonProperty("department_name")
	private String departmentName=null;
	@JsonProperty("request_category")
	private String requestCategory=null;
	@JsonProperty("project_name")
	private String projectName=null;
	@JsonProperty("resolution_no")
	private Integer resolutionNo=null;
	@JsonProperty("resolution_date")
	private String resolutionDate=null;
	@JsonProperty("prebid_meeting_date")
	private String prebidMeetingDate=null;
	@JsonProperty("prebid_meeting_location")
	private String prebidMeetingLocation=null;
	@JsonProperty("issue_from_date")
	private String issueFromDate=null;
	@JsonProperty("issue_till_date")
	private String issueTillDate=null;
	@JsonProperty("publish_date")
	private String publishDate=null;
	@JsonProperty("technical_bid_open_date")
	private String technicalBidOpenDate=null;
	@JsonProperty("financial_bid_open_date")
	private String financialBidOpenDate=null;
	@JsonProperty("validity")
	private Integer validity=null;
	@JsonProperty("upload_document")
	private String uploadDocument=null;
	/*
	 * @JsonProperty("work_no") private String workNo=null;
	 * 
	 * @JsonProperty("work_description") private String workDescription=null;
	 * 
	 * @JsonProperty("estimated_cost") private String estimatedCost=null;
	 * 
	 * @JsonProperty("tender_type") private String tenderType=null;
	 * 
	 * @JsonProperty("tender_fee") private Integer tenderFee=null;
	 * 
	 * @JsonProperty("emd") private String emd=null;
	 * 
	 * @JsonProperty("vendor_class") private String vendorClass=null;
	 * 
	 * @JsonProperty("work_duration") private String workDuration=null;
	 */
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("tenantId")
	 private String tenantId = null;
	
}
