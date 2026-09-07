package org.egov.garbageservice.web.models.bill;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.egov.garbageservice.web.models.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

/**
 * Contract model for a complete user garbage bill returned from or sent to the
 * billing/collection service (REST or Kafka).
 *
 * Behavior:
 * - Holds payer details (mobileNumber, payerName, payerId, etc.), bill header fields
 *   (billNumber, billDate, consumerCode, businessService, totalAmount, amountPaid).
 * - Tracks bill lifecycle via {@link StatusEnum} (ACTIVE, PAID, CANCELLED, EXPIRED, etc.).
 * - Contains a list of {@link BillDetail} rows (one per demand/period) with nested tax-head lines.
 * - Maps to/from JSON using Jackson {@code @JsonProperty}; supports collection rules
 *   (partPaymentAllowed, minimumAmountToBePaid, collectionModesNotAllowed).
 * - {@link #addBillDetail(BillDetail)} appends a bill line and skips duplicates.
 *
 * Notes:
 * - Data-only model; bill generation, search, cancel, and payment flows use this via
 *   {@link BillRepository} and services such as GarbageBillService and GarbageAccountService.
 * - Field names must align with the billing/collection service API schema.
 * - {@link StatusEnum#fromValue(String)} and {@link StatusEnum#contains(String)} assist JSON parsing.
 */
public class Bill {
    // TODO some of the fields are mandatory in yml, lets discuss billdetail and billaccountdetail also for more clarity

    @JsonProperty("id")
    @CustomSafeHtml
    private String id = null;

    @JsonProperty("mobileNumber")
    @CustomSafeHtml
    private String mobileNumber = null;

    @JsonProperty("paidBy")
    @CustomSafeHtml
    private String paidBy = null;

    @JsonProperty("payerName")
    @CustomSafeHtml
    private String payerName = null;

    @JsonProperty("payerAddress")
    @CustomSafeHtml
    private String payerAddress = null;

    @JsonProperty("payerEmail")
    @CustomSafeHtml
    private String payerEmail = null;

    @JsonProperty("payerId")
    @CustomSafeHtml
    private String payerId = null;

    @JsonProperty("status")
    private StatusEnum status = null;

    @JsonProperty("reasonForCancellation")
    @CustomSafeHtml
    private String reasonForCancellation = null;

    @JsonProperty("isCancelled")
    private Boolean isCancelled = null;

    @JsonProperty("additionalDetails")
    private JsonNode additionalDetails = null;

    @JsonProperty("billDetails")
//	  @Valid
    private List<BillDetail> billDetails = null;

    @JsonProperty("tenantId")
    @CustomSafeHtml
    private String tenantId = null;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails = null;

    @JsonProperty("collectionModesNotAllowed")
    private List<String> collectionModesNotAllowed = null;

    @JsonProperty("partPaymentAllowed")
    private Boolean partPaymentAllowed = null;

    @JsonProperty("isAdvanceAllowed")
    private Boolean isAdvanceAllowed;

    @JsonProperty("minimumAmountToBePaid")
    private BigDecimal minimumAmountToBePaid = null;

    @JsonProperty("businessService")
    @CustomSafeHtml
    private String businessService = null;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount = null;

    @JsonProperty("consumerCode")
    @CustomSafeHtml
    private String consumerCode = null;

    @JsonProperty("billNumber")
    @CustomSafeHtml
    private String billNumber = null;

    @JsonProperty("billDate")
    private Long billDate = null;

    @JsonProperty("amountPaid")
    private BigDecimal amountPaid;


    public enum StatusEnum {
        ACTIVE("ACTIVE"),

        CANCELLED("CANCELLED"),

        PAID("PAID"),

        PARTIALLY_PAID("PARTIALLY_PAID"),

        PAYMENT_CANCELLED("PAYMENT_CANCELLED"),

        EXPIRED("EXPIRED");

        private String value;

        /**
         * Constructs the enum with the specified string value.
         *
         * @param value the string representation of the bill status
         */

        StatusEnum(String value) {
            this.value = value;
        }

        /**
         * Checks if the given string represents a valid bill status.
         *
         * @param test the string to validate against known statuses
         * @return true if the string matches a known status, false otherwise
         */

        public static boolean contains(String test) {
            for (StatusEnum val : StatusEnum.values()) {
                if (val.name().equalsIgnoreCase(test)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Resolves a string value to its corresponding {@link StatusEnum} constant during JSON deserialization.
         *
         * @param text the string value representing the status
         * @return the matching {@link StatusEnum}, or null if no match is found
         */

        @JsonCreator
        public static StatusEnum fromValue(String text) {
            for (StatusEnum b : StatusEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        /**
         * Returns the string representation of the bill status.
         *
         * @return the status value as a string
         */

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

    }


}
