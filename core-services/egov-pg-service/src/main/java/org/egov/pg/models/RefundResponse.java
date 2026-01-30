package org.egov.pg.models;

import java.util.List;

import javax.validation.Valid;

import org.egov.pg.web.models.ResponseInfo;

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
public class RefundResponse {

	
	 @JsonProperty("ResponseInfo")
	    @Valid
	    private ResponseInfo responseInfo;

	    @JsonProperty("Refund")
	    @Valid
	    private List<Refund> refunds;
}
