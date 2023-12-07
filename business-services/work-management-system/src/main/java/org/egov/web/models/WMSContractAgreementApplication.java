package org.egov.web.models;

import java.util.List;

import javax.validation.Valid;

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
	
	/*
	 * @JsonProperty("agreement_no") private String agreementNo=null;
	 */
	
	@JsonProperty("agreementInfo")
	//private AgreementInfo agreementInfo = null;
	@Valid
	private List<AgreementInfo> agreementInfo = null;
	
	@JsonProperty("party1Details")
	private Party1Details party1Details = null;
	
	@JsonProperty("sDPGBGDetails")
	private SDPGBGDetails sDPGBGDetails = null;
	
	@JsonProperty("termsAndConditions")
	private TermsAndConditions termsAndConditions = null;
	
	/*
	 * @JsonProperty("party2Details") private Party2Details party2Details = null;
	 */
	
	@JsonProperty("contractors")
	private Contractors contractors = null;
	
	@JsonProperty("party2Witness")
	private Party2Witness party2Witness = null;
	
	@JsonProperty("agreementDocuments")
	private AgreementDocuments agreementDocuments = null;
	
	/*
	 * @JsonProperty("designation") private String designation=null;
	 * 
	 * @JsonProperty("employee_name") private String employeeName=null;
	 * 
	 * @JsonProperty("witness_name") private String witnessName=null;
	 * 
	 * @JsonProperty("address") private String address=null;
	 * 
	 * @JsonProperty("uid") private String uid=null;
	 */
	
	/*
	 * @JsonProperty("sr_no") private Integer srNo=null;
	 * 
	 * @JsonProperty("terms_and_conditions") private String termsAndConditions=null;
	 */
	
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("tenantId")
	 private String tenantId = null;
	
}
