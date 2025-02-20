package org.egov.pt.models;

import java.util.Date;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateTaxRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date fromDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date toDate;

	@JsonProperty("propertyIds")
	private Set<String> propertyIds;

	@JsonProperty("ulbNames")
	private Set<String> ulbNames;

	@JsonProperty("wardNumbers")
	private Set<String> wardNumbers;

	@JsonProperty("mobileNumbers")
	private Set<String> mobileNumbers;

	@JsonProperty("finYear")
	private String finYear;

}
