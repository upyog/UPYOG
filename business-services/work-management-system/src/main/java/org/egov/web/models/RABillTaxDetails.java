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
public class RABillTaxDetails {
	
	
	  @JsonProperty("running_account_id") 
	  private String runningAccountId=null;
	  
	  @JsonProperty("rabtd_id") 
	  private String rabtdId=null;
	  
	  @JsonProperty("sr_no")
		private Integer srNo=null;
	  
	  @JsonProperty("tax_Category")
		private String taxCategory=null;
	  
	  @JsonProperty("addition_deduction")
		private String additionDeduction=null;
	  
	  @JsonProperty("amountpercentage")
		private String amountPercentage=null;
	  
	  @JsonProperty("percentage_Value")
		private String percentageValue=null;
	  
	  @JsonProperty("amount")
		private String amount=null;
	  
	  @JsonProperty("total")
		private String total=null;

}
