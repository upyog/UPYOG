package org.upyog.cdwm.calculator.web.models;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;

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
public class CalculationResponse {

	@JsonProperty("ResponseInfo")
	@Valid
	private ResponseInfo responseInfo = null;

	@JsonProperty("Calculations")
	@Valid
	private BigDecimal calculations = null;

	
	
}
