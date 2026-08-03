package org.egov.pg.models;

import java.util.List;

import org.egov.pg.web.models.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
	
	  @JsonProperty("ResponseInfo")
	    private ResponseInfo responseInfo;

	    @JsonProperty("Payments")
	    private List<CollectionPayment> payments;

}
