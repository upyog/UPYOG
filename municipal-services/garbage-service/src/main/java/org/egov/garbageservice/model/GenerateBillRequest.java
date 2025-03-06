package org.egov.garbageservice.model;

import java.util.Date;
import java.util.List;

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
public class GenerateBillRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("grbgApplicationNumbers")
	private List<String> grbgApplicationNumbers;

	@JsonProperty("ulbNames")
	private List<String> ulbNames;

	@JsonProperty("wardNumbers")
	private List<String> wardNumbers;

	@JsonProperty("mobileNumbers")
	private List<String> mobileNumbers;

	@JsonProperty("month")
	private String month;

	@JsonProperty("year")
	private String year;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date fromDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date toDate;

}
