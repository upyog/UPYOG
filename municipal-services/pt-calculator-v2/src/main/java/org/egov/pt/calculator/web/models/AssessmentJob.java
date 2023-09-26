package org.egov.pt.calculator.web.models;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentJob {
	
	@NotNull
	private String tenantId;

	private String uuid;

	private String financialYear;
	
	private String status;
	
	private String locality;
	
	private int assessmentstobegenerated;
	
	private int successfulAssessments;
	
	private int failedAssessments;
	
	private String error;
	
	private JsonNode additionaldetails;
	
	private Long createdTime;

}
