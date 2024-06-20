package org.egov.web.models;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class WMSPhysicalMileStoneActivityApplication {
	
	@JsonProperty("pma_id") 
	private String pmaId=null;
	
	@JsonProperty("description_of_the_item") 
	private String descriptionOfTheItem=null;
	@JsonProperty("percentage_weightage")
	private String percentageWeightage=null;
	@JsonProperty("start_date")
	private String startDate=null;
	@JsonProperty("end_date")
	private String endDate=null;
	
	
	
	 
	 @JsonProperty("tenantId")
	 private String tenantId = null;
	 
	 @JsonProperty("auditDetails")
	 private AuditDetails auditDetails = null;
	 
	  
}
