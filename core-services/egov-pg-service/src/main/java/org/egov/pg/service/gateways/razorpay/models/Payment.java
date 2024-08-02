package org.egov.pg.service.gateways.razorpay.models;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Payment {
	@JsonProperty("id")
	private String paymentId;

	@JsonProperty("entity")
	private String paymentEntity;

	@JsonProperty("amount")
	private int paymentAmount;

	@JsonProperty("currency")
	private String paymentCurrency;

	@JsonProperty("status")
	private String paymentStatus;

	@JsonProperty("order_id")
	private String orderId;

	@JsonProperty("invoice_id")
	private String invoiceId;

	@JsonProperty("international")
	private boolean international;

	@JsonProperty("method")
	private String paymentMethod;

	@JsonProperty("amount_refunded")
	private int amountRefunded;

	@JsonProperty("refund_status")
	private String refundStatus;

	@JsonProperty("captured")
	private boolean captured;

	@JsonProperty("description")
	private String paymentDescription;

	@JsonProperty("card_id")
	private String cardId;

	@JsonProperty("card")
	private LinkedHashMap<String, Object> card;

	@JsonProperty("bank")
	private String bank;

	@JsonProperty("wallet")
	private String wallet;

	@JsonProperty("vpa")
	private String vpa;

	@JsonProperty("email")
	private String email;

	@JsonProperty("contact")
	private String contact;

	@JsonProperty("notes")
	Object notes;

	@JsonProperty("fee")
	private int fee;

	@JsonProperty("tax")
	private int tax;

	@JsonProperty("error_code")
	private String errorCode;

	@JsonProperty("error_description")
	private String errorDescription;

	@JsonProperty("error_source")
	private String errorSource;

	@JsonProperty("error_step")
	private String errorStep;

	@JsonProperty("error_reason")
	private String errorReason;

	@JsonProperty("acquirer_data")
	private LinkedHashMap<String, Object> acquirerData;

	@JsonProperty("created_at")
	private int createdAt;
}
