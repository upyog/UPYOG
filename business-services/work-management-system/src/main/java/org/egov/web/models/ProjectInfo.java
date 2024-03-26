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
public class ProjectInfo {
	
	
	  @JsonProperty("running_account_id") 
	  private String runningAccountId=null;
	  
	  @JsonProperty("rafb_id") 
	  private String rafbId=null;
	  
	  @JsonProperty("project_name")
		private String projectName=null;
	  
	  @JsonProperty("work_name")
		private String workName=null;
	  
	  @JsonProperty("workOrder_No")
		private String workOrderNo=null;

}
