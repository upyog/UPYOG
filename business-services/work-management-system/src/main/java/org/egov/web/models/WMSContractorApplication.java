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
public class WMSContractorApplication {
	
	@JsonProperty("vendor_id")
	private Integer vendorId=null;
	@JsonProperty("vendor_type")
	private String vendorType=null;
	@JsonProperty("vendor_sub_type")
	private String vendorSubType=null;
	@JsonProperty("vendor_name")
	private String vendorName=null;
	@JsonProperty("vendor_status")
	private String vendorStatus=null;
	@Column(unique=true)
	@JsonProperty("pfms_vendor_code")
	private String PFMSVendorCode=null;
	@JsonProperty("payto")
	private String payTo=null;
	@JsonProperty("mobile_number")
	private Long mobileNumber=null;
	@JsonProperty("email")
	private String email=null;
	@JsonProperty("uid_number")
	private Long UIDNumber=null;
	@JsonProperty("gst_number")
	private Long GSTNumber=null;
	@JsonProperty("pan_number")
	private String PANNumber=null;
	@Column(unique=true)
	@JsonProperty("bank_branch_ifsc_code")
	private String bankBranchIfscCode=null;
	@JsonProperty("bank_account_number")
	private Long bankAccountNumber=null;
	@JsonProperty("function")
	private String function=null;
	@JsonProperty("primary_account_head")
	private String primaryAccountHead=null;
	@JsonProperty("vendor_class")
	private String vendorClass=null;
	@JsonProperty("address")
	private String address=null;
	@JsonProperty("epfo_account_number")
	private String EPFOAccountNumber=null;
	@JsonProperty("vat_number")
	private String vatNumber=null;
	@JsonProperty("allow_direct_payment")
	private String allowDirectPayment=null;
	@JsonProperty("tenantId")
	 private String tenantId = null;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	
}
