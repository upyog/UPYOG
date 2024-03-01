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
	
	
	  @Valid 
	  private List<AgreementInfo> agreementInfo = null;
	 
	
	@JsonProperty("party1Details")
	@Valid 
	private List<Party1Details> party1Details = null;
	
	@JsonProperty("sDPGBGDetails")
	private List<SDPGBGDetails> sDPGBGDetails = null;
	
	@JsonProperty("termsAndConditions")
	private List<TermsAndConditions> termsAndConditions = null;
	
	/*
	 * @JsonProperty("party2Details") private Party2Details party2Details = null;
	 */
	
	@JsonProperty("contractors")
	private List<Contractors> contractors = null;
	
	@JsonProperty("party2Witness")
	private List<Party2Witness> party2Witness = null;
	
	@JsonProperty("agreementDocuments")
	private List<AgreementDocuments> agreementDocuments = null;
	
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
        if (!this.agreementInfo.stream().anyMatch(x -> x.getAgrId().equalsIgnoreCase(agreementInfoItem.getAgrId())))    
        this.agreementInfo.add(agreementInfoItem);
        return this;
    }
	
	public WMSContractAgreementApplication addParty1Details(Party1Details party1DetailsItem) {
        if(this.party1Details==null)
            this.party1Details = new ArrayList<>();
        //if(!this.party1Details.contains(party1DetailsItem))
        //if(this.party1Details.stream().filter(x-> x.getUidP1().equalsIgnoreCase(party1DetailsItem.getUidP1())) == null)
        if (!this.party1Details.stream().anyMatch(x -> x.getParty1Id().equalsIgnoreCase(party1DetailsItem.getParty1Id())))    
        this.party1Details.add(party1DetailsItem);
        return this;
    }
	
	public WMSContractAgreementApplication addSDPGBGDetails(SDPGBGDetails sDPGBGDetailsItem) {
        if(this.sDPGBGDetails==null)
            this.sDPGBGDetails = new ArrayList<>();
        //if(this.agreementInfo.stream().filter(x-> x.getAgreementName().equalsIgnoreCase(agreementInfoItem.getAgreementName())) == null)
        if (!this.sDPGBGDetails.stream().anyMatch(x -> x.getSdpgId().equalsIgnoreCase(sDPGBGDetailsItem.getSdpgId())))    
        this.sDPGBGDetails.add(sDPGBGDetailsItem);
        return this;
    }
	
	public WMSContractAgreementApplication addTermsAndConditions(TermsAndConditions termsAndConditionsItem) {
        if(this.termsAndConditions==null)
            this.termsAndConditions = new ArrayList<>();
        //if(this.agreementInfo.stream().filter(x-> x.getAgreementName().equalsIgnoreCase(agreementInfoItem.getAgreementName())) == null)
        if (!this.termsAndConditions.stream().anyMatch(x -> x.getTncId().equalsIgnoreCase(termsAndConditionsItem.getTncId())))    
        this.termsAndConditions.add(termsAndConditionsItem);
        return this;
    }
	
	public WMSContractAgreementApplication addAgreementDocuments(AgreementDocuments agreementDocumentsItem) {
        if(this.agreementDocuments==null)
            this.agreementDocuments = new ArrayList<>();
        //if(this.agreementInfo.stream().filter(x-> x.getAgreementName().equalsIgnoreCase(agreementInfoItem.getAgreementName())) == null)
        if (!this.agreementDocuments.stream().anyMatch(x -> x.getAdId().equalsIgnoreCase(agreementDocumentsItem.getAdId())))    
        this.agreementDocuments.add(agreementDocumentsItem);
        return this;
    }
	
	public WMSContractAgreementApplication addParty2Witness(Party2Witness party2WitnessItem) {
        if(this.party2Witness==null)
            this.party2Witness = new ArrayList<>();
        //if(this.agreementInfo.stream().filter(x-> x.getAgreementName().equalsIgnoreCase(agreementInfoItem.getAgreementName())) == null)
        if (!this.party2Witness.stream().anyMatch(x -> x.getPwId().equalsIgnoreCase(party2WitnessItem.getPwId())))    
        this.party2Witness.add(party2WitnessItem);
        return this;
    }
	
	public WMSContractAgreementApplication addContractors(Contractors contractorsItem) {
        if(this.contractors==null)
            this.contractors = new ArrayList<>();
        //if(this.agreementInfo.stream().filter(x-> x.getAgreementName().equalsIgnoreCase(agreementInfoItem.getAgreementName())) == null)
        if (!this.contractors.stream().anyMatch(x -> x.getConId().equalsIgnoreCase(contractorsItem.getConId())))    
        this.contractors.add(contractorsItem);
        return this;
    }
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("tenantId")
	 private String tenantId = null;
	
}
