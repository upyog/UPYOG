package org.egov.pg.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResponse {
	private String id;
	private String entity;
	private String amount;
	private String amountPaid;
	private String amountDue;
	private String currency;
	private String receipt;
	private String offerId;
	private String status;
	private int attempts;
	private String notes;
	private Long createdAt;
	
	

}
