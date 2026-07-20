package org.egov.garbageservice.model;

import java.math.BigDecimal;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Tax-head line item companion to BillV2 for amount breakdown in v2 bill flows.
 * Aligns with billing service account detail fields used in garbage PDF and payment views.
 */
@Builder
public class BillAccountDetailV2   {
	
  @JsonProperty("id")
  @CustomSafeHtml
  private String id;

  @JsonProperty("tenantId")
  @CustomSafeHtml
  private String tenantId;

  @JsonProperty("billDetailId")
  @CustomSafeHtml
  private String billDetailId;

  @JsonProperty("demandDetailId")
  @CustomSafeHtml
  private String demandDetailId;

  @JsonProperty("order")
  private Integer order;

  @JsonProperty("amount")
  private BigDecimal amount;
  
  @JsonProperty("adjustedAmount")
  private BigDecimal adjustedAmount;

  @JsonProperty("taxHeadCode")
  @CustomSafeHtml
  private String taxHeadCode;

  @JsonProperty("additionalDetails")
  private Object additionalDetails;

  @JsonProperty("auditDetails")
  private AuditDetails auditDetails;
}

