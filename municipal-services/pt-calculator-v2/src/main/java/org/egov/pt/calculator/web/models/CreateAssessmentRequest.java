package org.egov.pt.calculator.web.models;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CreateAssessmentRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	private String assessmentYear;

	private List<String> locality;

	private List<String> propertyType;
	private Long offset;

	private Long limit;
	private String tenantId;

	private Boolean isRented = true;

}