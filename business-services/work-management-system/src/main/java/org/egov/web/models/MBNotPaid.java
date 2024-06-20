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
public class MBNotPaid {
	
	
	  @JsonProperty("running_account_id") 
	  private String runningAccountId=null;
	  
	  @JsonProperty("mnp_id") 
	  private String mnpId=null;
	  
	  @JsonProperty("select_measurement_book")
		private String projectName=null;
	  
	  @JsonProperty("mb_date")
		private String mbDate=null;
	  
	  @JsonProperty("mb_no")
		private Integer mbNo=null;
	  
	  @JsonProperty("amount")
		private Integer amount=null;

}
