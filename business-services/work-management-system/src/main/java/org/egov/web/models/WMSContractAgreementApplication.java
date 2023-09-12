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
public class WMSContractAgreementApplication {
	
	@JsonProperty("agreement_no")
	private String agreementNo=null;
	@JsonProperty("agreement_name")
	private String agreementName=null;
	@JsonProperty("agreement_date")
	private String agreementDate=null;
	@JsonProperty("department_name")
	private String departmentName=null;
	@JsonProperty("loa_no")
	private String loaNo=null;
	@JsonProperty("resolution_no")
	private Integer resolutionNo=null;
	@JsonProperty("resolution_date")
	private String resolutionDate=null;
	@JsonProperty("tender_no")
	private Integer tenderNo=null;
	@JsonProperty("tender_date")
	private String tenderDate=null;
	@JsonProperty("agreement_type")
	private String agreementType=null;
	@JsonProperty("defect_liability_period")
	private String defectLiabilityPeriod=null;
	@JsonProperty("contract_period")
	private String contractPeriod=null;
	@JsonProperty("agreement_amount")
	private Integer agreementAmount=null;
	@JsonProperty("payment_type")
	private String paymentType=null;
	@JsonProperty("deposit_type")
	private String depositType=null;
	@JsonProperty("deposit_amount")
	private Integer depositAmount=null;
	@JsonProperty("work_description")
	private String workDescription=null;
	@JsonProperty("account_no")
	private Long accountNo=null;
	@JsonProperty("particulars")
	private String particulars=null;
	@JsonProperty("valid_from_date")
	private String validFromDate=null;
	@JsonProperty("valid_till_date")
	private String validTillDate=null;
	@JsonProperty("bank_branch_ifsc_code")
	private String bankBranchIfscCode=null;
	@JsonProperty("payment_mode")
	private String paymentMode=null;
	@JsonProperty("designation")
	private String designation=null;
	@JsonProperty("employee_name")
	private String employeeName=null;
	@JsonProperty("witness_name")
	private String witnessName=null;
	@JsonProperty("address")
	private String address=null;
	@JsonProperty("uid")
	private String uid=null;
	@JsonProperty("vendor_type")
	private String vendorType=null;
	@JsonProperty("vendor_name")
	private String vendorName=null;
	@JsonProperty("represented_by")
	private String representedBy=null;
	@JsonProperty("primary_party")
	private String primaryParty=null;
	@JsonProperty("sr_no")
	private Integer srNo=null;
	@JsonProperty("terms_and_conditions")
	private String termsAndConditions=null;
	@JsonProperty("document_description")
	private String documentDescription=null;
	@JsonProperty("upload_document")
	private String uploadDocument=null;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("tenantId")
	 private String tenantId = null;
	
}
