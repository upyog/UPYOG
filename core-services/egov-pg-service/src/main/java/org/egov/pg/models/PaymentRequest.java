package org.egov.pg.models;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.web.models.TransactionRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
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
public class PaymentRequest {

	@JsonProperty("RequestInfo")
	@Valid
	private RequestInfo requestInfo;

	@JsonProperty("Payment")
	@Valid
	private CollectionPayment payment;

}
