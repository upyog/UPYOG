package org.egov.web.models;

import javax.persistence.Column;

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
public class WMSProjectRegisterApplication {
	@JsonProperty("register_id")
	private String registerId=null;
	@JsonProperty("scheme_name")
	private String schemeName=null;
	@JsonProperty("project_name")
	private String projectName=null;
	@JsonProperty("work_name")
	private String workName=null;
	@JsonProperty("work_type")
	private String workType=null;
	/*
	 * @JsonProperty("percent_weightage") private String percentWeightage=null;
	 */
	
	
	@JsonProperty("estimated_number")
	private Integer estimatedNumber=null;
	@JsonProperty("estimated_work_cost")
	private String estimatedWorkCost=null;
	@JsonProperty("sanctioned_tender_amount")
	private Long sanctionedTenderAmount=null;
	@JsonProperty("status_name")
	private String statusName=null;
	@JsonProperty("bill_received_till_date")
	private String billReceivedTillDate=null;
	/*
	 * @JsonProperty("payment_received_till_date") private String
	 * paymentReceivedTillDate=null;
	 */
	
	@JsonProperty("tenantId")
	 private String tenantId = null;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	
}
