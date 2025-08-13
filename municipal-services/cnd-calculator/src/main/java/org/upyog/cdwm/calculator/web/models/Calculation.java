package org.upyog.cdwm.calculator.web.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class Calculation {


	@JsonProperty("applicationNo")
	private String applicationNumber = null;

	@JsonProperty("cndApplicationDetail")
	private CNDApplicationDetail cndRequest = null;

	@NotNull
	@JsonProperty("tenantId")
	@Size(min = 2, max = 256)
	private String tenantId = null;
	
	@JsonProperty("feeType") 
	String feeType = null;
}
