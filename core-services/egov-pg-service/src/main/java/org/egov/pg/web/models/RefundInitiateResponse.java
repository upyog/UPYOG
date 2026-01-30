package org.egov.pg.web.models;

import javax.validation.Valid;

import org.egov.pg.models.Refund;

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
public class RefundInitiateResponse {

	
	 @JsonProperty("ResponseInfo")
	    @Valid
	    private ResponseInfo responseInfo;

	    @JsonProperty("Refund")
	    @Valid
	    private Refund refunds;
}
