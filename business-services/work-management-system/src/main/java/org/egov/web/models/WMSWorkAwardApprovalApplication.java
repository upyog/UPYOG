package org.egov.web.models;

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
public class WMSWorkAwardApprovalApplication {
	
	@JsonProperty("work_award_id")
	private String workAwardId=null;
	@JsonProperty("work_no")
	private String workNo=null;
	@JsonProperty("work_name")
	private String workName=null;
	@JsonProperty("percentage_type")
	private String percentageType=null;
	@JsonProperty("quoted_percentage")
	private String quotedPercentage=null;
	@JsonProperty("accepted_work_cost")
	private String acceptedWorkCost=null;
	@JsonProperty("contractor_name")
	private String contractorName=null;
	@JsonProperty("no_of_days_for_agreement")
	private Integer noOfDaysForAgreement=null;
	@JsonProperty("loa_generation")
	private String loaGeneration=null;
	@JsonProperty("award_date")
	private String awardDate=null;
	@JsonProperty("document_upload")
	private String documentUpload=null;
	@JsonProperty("award_status")
	private String awardStatus=null;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("tenantId")
	private String tenantId = null;
	
}
