package org.egov.asset.web.models.calcontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;

import javax.validation.Valid;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculationRes {

	@JsonProperty("ResponseInfo")
	@Valid
	private ResponseInfo responseInfo = null;

	@JsonProperty("Message")
	@Valid
	private String message;

	
	
}
