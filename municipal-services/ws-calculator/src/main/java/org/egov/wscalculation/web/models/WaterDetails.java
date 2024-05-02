package org.egov.wscalculation.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WaterDetails {

	@JsonProperty("connectionNo")
	private String connectionNo = null;
	

	@JsonProperty("connectionExecutionDate")
	private Long connectionExecutionDate = null;
	
}
