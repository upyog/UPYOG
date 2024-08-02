package org.egov.pg.service.gateways.razorpay.models;

import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Order {
	@JsonProperty("id")
	private String orderId;

	@JsonProperty("entity")
	private String orderEntity;

	@JsonProperty("amount")
	private int orderAmount;

	@JsonProperty("amount_paid")
	private int amountPaid;

	@JsonProperty("amount_due")
	private int amountDue;

	@JsonProperty("currency")
	private String orderCurrency;

	@JsonProperty("receipt")
	private String orderReceipt;

	@JsonProperty("offer_id")
	private String offerId;

	@JsonProperty("status")
	private String orderStatus;

	@JsonProperty("attempts")
	private int orderAttempts;

	@JsonProperty("notes")
	private List<LinkedHashMap<Object, Object>> orderNotes;

	@JsonProperty("created_at")
	private int createdAt;
}
