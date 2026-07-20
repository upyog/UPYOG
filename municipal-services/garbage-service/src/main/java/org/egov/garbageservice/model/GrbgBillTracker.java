package org.egov.garbageservice.model;

import java.math.BigDecimal;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
/**
 * Domain model tracking monthly garbage bill generation state per application.
 * Links application, demand, bill id, period, ward, amounts, and generation status for scheduler reconciliation.
 */
@AllArgsConstructor
public class GrbgBillTracker {

	@CustomSafeHtml
	private String uuid;
	@CustomSafeHtml
	private String grbgApplicationId;
	@CustomSafeHtml
	private String tenantId;
	@CustomSafeHtml
	private String month;
	@CustomSafeHtml
	private String year;
	@CustomSafeHtml
	private String fromDate;
	@CustomSafeHtml
	private String toDate;
	@CustomSafeHtml
	private String ward;
	@CustomSafeHtml
	private String billId;
	@CustomSafeHtml
	private String demandId;
	@CustomSafeHtml
	private String consumerCode;
	@CustomSafeHtml
	private String status = "ACTIVE";
	@Builder.Default
	@CustomSafeHtml
	private String type = "GENERAL";
	private BigDecimal grbgBillAmount;
	private AuditDetails auditDetails;
	@JsonProperty("additionaldetail")
	private JsonNode additionaldetail;
	private Long expiryDate;                  
	private BigDecimal grbgBillWithoutPenalty;
	private BigDecimal penaltyAmount;
	private BigDecimal rebateAmount;
	private BigDecimal garbageBillWithoutRebate;

}
