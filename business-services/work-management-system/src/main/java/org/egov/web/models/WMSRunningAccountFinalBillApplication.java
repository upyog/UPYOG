package org.egov.web.models;

import java.util.ArrayList;
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
public class WMSRunningAccountFinalBillApplication {
	
	@JsonProperty("running_account_id")
	private String runningAccountId=null;
	
	@JsonProperty("previousRunningBillInfo")
	@Valid 
	private List<PreviousRunningBillInfo> previousRunningBillInfo = null;
	 
	
	/*
	 * @JsonProperty("projectInfo")
	 * 
	 * @Valid private List<ProjectInfo> projectInfo = null;
	 * 
	 * @JsonProperty("mBNotPaid")
	 * 
	 * @Valid private List<MBNotPaid> mBNotPaid = null;
	 * 
	 * @JsonProperty("tenderWorkDetails")
	 * 
	 * @Valid private List<TenderWorkDetails> tenderWorkDetails = null;
	 * 
	 * @JsonProperty("withHeldDeductionsDetails")
	 * 
	 * @Valid private List<WithHeldDeductionsDetails> withHeldDeductionsDetails =
	 * null;
	 * 
	 * @JsonProperty("rABillTaxDetails")
	 * 
	 * @Valid private List<RABillTaxDetails> rABillTaxDetails = null;
	 */
	
	
	
	
	
	
	
	@JsonProperty("percentage_type")
	private String percentageType=null;
	@JsonProperty("award_amount")
	private Integer awardAmount=null;
	
	@JsonProperty("deduction_amount")
	private Integer deductionAmount=null;
	
	@JsonProperty("deduction_description")
	private String deductionDescription=null;
	
	@JsonProperty("calculation_method")
	private String calculationMethod=null;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("tenantId")
	 private String tenantId = null;
	
	
	public WMSRunningAccountFinalBillApplication addRunningAccountFinalBill(PreviousRunningBillInfo previousRunningBillInfoItem) {
        if(this.previousRunningBillInfo==null)
            this.previousRunningBillInfo = new ArrayList<>();
        //if(this.agreementInfo.stream().filter(x-> x.getAgreementName().equalsIgnoreCase(agreementInfoItem.getAgreementName())) == null)
        if (!this.previousRunningBillInfo.stream().anyMatch(x -> x.getPrbiId().equalsIgnoreCase(previousRunningBillInfoItem.getPrbiId())))    
        this.previousRunningBillInfo.add(previousRunningBillInfoItem);
        return this;
    }
	
}
