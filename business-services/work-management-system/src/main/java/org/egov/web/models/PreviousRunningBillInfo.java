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
public class PreviousRunningBillInfo {
	
	
	  @JsonProperty("running_account_id") 
	  private String runningAccountId=null;
	  
	  @JsonProperty("prbi_id") 
	  private String prbiId=null;
	  
	  @JsonProperty("running_account_bill_date")
		private String runningAccountBillDate=null;
	  
	  @JsonProperty("running_account_bill_no")
		private String runningAccountBillNo=null;
	  
	  @JsonProperty("running_account_bill_amount")
		private String runningAccountBillAmount=null;
	  
	  @JsonProperty("tax_Amount")
		private String taxAmount=null;
	  
	  @JsonProperty("remark")
		private String remark=null;

}
