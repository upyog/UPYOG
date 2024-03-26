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
public class TenderWorkDetails {
	
	
	  @JsonProperty("running_account_id") 
	  private String runningAccountId=null;
	  
	  @JsonProperty("twd_id") 
	  private String twdId=null;
	  
	  @JsonProperty("estimated_work_cost")
		private String estimatedWorkCost=null;
	  
	  @JsonProperty("work_name")
		private String workName=null;
	  
	  @JsonProperty("tender_type")
		private String tenderType=null;
	  
	  @JsonProperty("value")
		private String value=null;
	  
	  @JsonProperty("amount")
		private Integer amount=null;

}
