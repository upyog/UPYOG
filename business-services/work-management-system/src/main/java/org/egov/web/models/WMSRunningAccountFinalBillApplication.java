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
public class WMSRunningAccountFinalBillApplication {
	
	@JsonProperty("running_account_id")
	private String runningAccountId=null;
	@JsonProperty("project_name")
	private String projectName=null;
	@JsonProperty("work_name")
	private String workName=null;
	@JsonProperty("mb_no")
	private Integer mbNo=null;
	@JsonProperty("mb_date")
	private String mbDate=null;
	@JsonProperty("mb_amount")
	private Integer mbAmount=null;
	@JsonProperty("estimated_cost")
	private String estimatedCost=null;
	@JsonProperty("tender_type")
	private String tenderType=null;
	@JsonProperty("value")
	private String value=null;
	@JsonProperty("percentage_type")
	private String percentageType=null;
	@JsonProperty("award_amount")
	private Integer awardAmount=null;
	@JsonProperty("bill_date")
	private String billDate=null;
	@JsonProperty("bill_no")
	private Integer billNo=null;
	@JsonProperty("bill_amount")
	private Integer billAmount=null;
	@JsonProperty("deduction_amount")
	private Integer deductionAmount=null;
	@JsonProperty("remark")
	private String remark=null;
	@JsonProperty("sr_no")
	private Integer srNo=null;
	@JsonProperty("deduction_description")
	private String deductionDescription=null;
	@JsonProperty("addition_deduction")
	private String additionDeduction=null;
	@JsonProperty("calculation_method")
	private String calculationMethod=null;
	@JsonProperty("percentage")
	private String percentage=null;
	@JsonProperty("percentage_Value")
	private String percentageValue=null;
	@JsonProperty("workOrder_No")
	private String workOrderNo=null;
	@JsonProperty("tax_Amount")
	private String taxAmount=null;
	@JsonProperty("tax_Category")
	private String taxCategory=null;
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("tenantId")
	 private String tenantId = null;
	
}
