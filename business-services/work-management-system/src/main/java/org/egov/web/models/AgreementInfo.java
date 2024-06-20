package org.egov.web.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "Collection of audit related fields used by most models")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2022-10-25T21:43:19.662+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgreementInfo {
	
	
	  @JsonProperty("agreement_no") 
	  private String agreementNo=null;
	  @JsonProperty("agr_id") 
	  private String agrId=null;
	@JsonProperty("agreement_name")
	private String agreementName=null;
	@JsonProperty("agreement_date")
	private String agreementDate=null;
	@JsonProperty("department_name_ai")
	private String departmentNameAi=null;
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

}
