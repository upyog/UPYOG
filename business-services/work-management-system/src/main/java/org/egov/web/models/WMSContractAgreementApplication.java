package org.egov.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

//import org.egov.tl.web.models.OwnerInfo;
//import org.egov.tl.web.models.TradeLicenseDetail;

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
	 
	
	@JsonProperty("agreementInfo")
	//private AgreementInfo agreementInfo = null;
	
	  @Valid 
	  private List<AgreementInfo> agreementInfo = null;
	 
	
	@JsonProperty("party1Details")
	@Valid 
	private List<Party1Details> party1Details = null;
	
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
	public WMSContractAgreementApplication addAgreementInfoItem(AgreementInfo agreementInfoItem) {
        if(this.agreementInfo==null)
            this.agreementInfo = new ArrayList<>();
        //if(this.agreementInfo.stream().filter(x-> x.getAgreementName().equalsIgnoreCase(agreementInfoItem.getAgreementName())) == null)
        if (!this.agreementInfo.stream().anyMatch(x -> x.getAgreementName().equalsIgnoreCase(agreementInfoItem.getAgreementName())))    
        this.agreementInfo.add(agreementInfoItem);
        return this;
    }
	
	public WMSContractAgreementApplication addParty1Details(Party1Details party1DetailsItem) {
        if(this.party1Details==null)
            this.party1Details = new ArrayList<>();
        //if(!this.party1Details.contains(party1DetailsItem))
        //if(this.party1Details.stream().filter(x-> x.getUidP1().equalsIgnoreCase(party1DetailsItem.getUidP1())) == null)
        if (!this.party1Details.stream().anyMatch(x -> x.getUidP1().equalsIgnoreCase(party1DetailsItem.getUidP1())))    
        this.party1Details.add(party1DetailsItem);
        return this;
    }
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("tenantId")
	 private String tenantId = null;
	
}
