package org.egov.garbageservice.contract.bill;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.*;

//import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

import org.egov.garbageservice.model.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
/**
 * Contract model for one tax-head charge line inside a {@link BillDetail}.
 *
 * Behavior:
 * - Represents a single levy on a bill line (taxHeadCode, amount, adjustedAmount, order).
 * - Links back to demand and bill detail via demandDetailId and billDetailId.
 * - Classifies the charge period/type using {@link Purpose} (CURRENT, ARREAR, penalty, rebate, etc.).
 * - Maps fields to/from JSON with Jackson for billing API and Kafka payloads.
 *
 * Notes:
 * - Nested under {@link BillDetail#getBillAccountDetails()}; a bill line can have multiple entries.
 * - This class carries data only; amount calculation and penalty logic live in garbage-service services.
 * - Field names must align with the billing/collection service schema from the producer/API.
 */
public class BillAccountDetail {

//	@Size(max=64)
	@JsonProperty("id")
	@CustomSafeHtml
	private String id = null;

//	@Size(max=64)
	@JsonProperty("tenantId")
	@CustomSafeHtml
	private String tenantId = null;

//	@Size(max=64)
	@JsonProperty("billDetailId")
	@CustomSafeHtml
	private String billDetailId = null;

//	@Size(max=64)
	@JsonProperty("demandDetailId")
	@CustomSafeHtml
	private String demandDetailId = null;

	@JsonProperty("order")
	private Integer order = null;

	@JsonProperty("amount")
	private BigDecimal amount = null;

	@JsonProperty("adjustedAmount")
	private BigDecimal adjustedAmount = null;

	@JsonProperty("isActualDemand")
	private Boolean isActualDemand = null;

//	@Size(max=64)
	@JsonProperty("taxHeadCode")
	@CustomSafeHtml
	private String taxHeadCode = null;

	@JsonProperty("additionalDetails")
	private JsonNode additionalDetails = null;

	@JsonProperty("purpose")
	private Purpose purpose = null;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
}
