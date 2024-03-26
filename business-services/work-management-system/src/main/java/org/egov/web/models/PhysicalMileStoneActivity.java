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
public class PhysicalMileStoneActivity {
	
	@JsonProperty("description_of_the_item") 
	private String descriptionOfTheItem=null;
	@JsonProperty("percentage_weightage")
	private String percentageWeightage=null;
	@JsonProperty("start_date")
	private String startDate=null;
	@JsonProperty("end_date")
	private String endDate=null;
	

}
