package org.egov.pg.service.gateways.razorpay.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentResponse {
	@JsonProperty("entity")
	private String entity;

	@JsonProperty("count")
	private int count;

	@JsonProperty("items")
	private List<Payment> payments;
}
