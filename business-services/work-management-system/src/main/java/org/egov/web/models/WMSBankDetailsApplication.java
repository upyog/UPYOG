package org.egov.web.models;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class WMSBankDetailsApplication {
	
	@JsonProperty("bank_id")
	private String bankId = null;
	
	@JsonProperty("bank_name")
    private String bankName = null;

	@JsonProperty("bank_branch")
	 private String bankBranch = null;
	
	@JsonProperty("bank_ifsc_code")
	 private String bankIfscCode = null;
	
	@JsonProperty("bank_branch_ifsc_code")
	 private String bankBranchIfscCode = null;
	
	@JsonProperty("status")
	 private String status = null;
	
	
	 
	 @JsonProperty("tenantId")
	 private String tenantId = null;
	 
	 @JsonProperty("auditDetails")
	 private AuditDetails auditDetails = null;
	 
	  
}
