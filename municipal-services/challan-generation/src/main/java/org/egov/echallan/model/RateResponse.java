package org.egov.echallan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.common.contract.response.ResponseInfo;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateResponse {
	
	@JsonProperty("responseInfo")
	private ResponseInfo responseInfo = null;
	
	@JsonProperty("amount")
	private BigDecimal amount = null;
	
	@JsonProperty("taxHeadCode")
	private String taxHeadCode = null;
	
	@JsonProperty("offenceTypeName")
	private String offenceTypeName = null;
}
