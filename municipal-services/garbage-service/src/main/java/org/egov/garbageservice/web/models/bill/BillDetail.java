package org.egov.garbageservice.web.models.bill;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.egov.garbageservice.web.models.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
/**
 * Contract model for one billing line inside a {@link Bill} when garbage-service exchanges
 * bill data with the billing/collection service (REST or Kafka).
 *
 * Behavior:
 * - Represents a single demand or billing period on a bill (fromPeriod, toPeriod, amount,
 *   amountPaid, expiryDate, collectionType, channel, etc.).
 * - Maps fields to/from JSON using Jackson {@code @JsonProperty} for billing API payloads.
 * - Holds nested {@link BillAccountDetail} entries in billAccountDetails for tax-head level
 *   breakdown (e.g. garbage charge, penalty).
 * - Used as part of {@link Bill#getBillDetails()} in bill generation, fetch, penalty update,
 *   PDF generation, and notification flows.
 * - {@link #addBillAccountDetail(BillAccountDetail)} adds a tax-head line and skips duplicates.
 *
 * Notes:
 * - This class carries data only; billing logic lives in services such as GarbageBillService
 *   and GarbageAccountService.
 * - Field names must align with the billing/collection service schema from the producer/API.
 * - Lombok provides builder, getters, and setters; equals/hashCode is based on id only.
 */
public class BillDetail {

    @JsonProperty("id")
    @CustomSafeHtml
    private String id = null;

    @JsonProperty("tenantId")
    @CustomSafeHtml
    private String tenantId = null;

    @JsonProperty("demandId")
    @CustomSafeHtml
    private String demandId = null;

    @JsonProperty("billId")
    @CustomSafeHtml
    private String billId = null;

    @JsonProperty("amount")
//	@NotNull
    private BigDecimal amount = null;

    @JsonProperty("amountPaid")
    private BigDecimal amountPaid = null;

    //	@NotNull
    @JsonProperty("fromPeriod")
    private Long fromPeriod = null;

    //	@NotNull
    @JsonProperty("toPeriod")
    private Long toPeriod = null;

    @JsonProperty("additionalDetails")
    private JsonNode additionalDetails = null;

    @JsonProperty("channel")
    @CustomSafeHtml
    private String channel = null;

    @JsonProperty("voucherHeader")
    @CustomSafeHtml
    private String voucherHeader = null;

    @JsonProperty("boundary")
    @CustomSafeHtml
    private String boundary = null;

    @JsonProperty("manualReceiptNumber")
    @CustomSafeHtml
    private String manualReceiptNumber = null;

    @JsonProperty("manualReceiptDate")
    private Long manualReceiptDate = null;


    @JsonProperty("billAccountDetails")
    private List<BillAccountDetail> billAccountDetails = null;

    //	@NotNull
    @JsonProperty("collectionType")
    @CustomSafeHtml
    private String collectionType = null;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails = null;

    @CustomSafeHtml
    private String billDescription;

    //	@NotNull
    @JsonProperty("expiryDate")
    private Long expiryDate;

    @CustomSafeHtml
    private String displayMessage;

    private Boolean callBackForApportioning;

    @CustomSafeHtml
    private String cancellationRemarks;

}
