package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessingUpdateRequest {

	private String billId;

	private String txnId;

	@JsonProperty("isPaymentProcessing")
	private boolean isPaymentProcessing = false;
	
	private String txnAmount;
}
