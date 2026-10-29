package org.egov.garbageservice.web.models.bill;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.User;
import org.egov.garbageservice.web.models.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Contract model for a revenue demand (assessment) before or alongside bill generation.
 * <p>
 * Behavior:
 * - Identifies the consumer (consumerCode, tenantId, businessService) and tax period
 * (taxPeriodFrom, taxPeriodTo).
 * - Holds payer {@link org.egov.common.contract.request.User}, status via {@link StatusEnum},
 * and a list of {@link DemandDetail} tax-head amounts.
 * - Supports bill expiry hints (fixedBillExpiryDate, billExpiryTime) and minimumAmountPayable.
 * - Maps to/from JSON for demand create, update, and search APIs.
 * <p>
 * Notes:
 * - Used with {@link DemandRepository} when garbage-service creates or updates demands on billing service.
 * - Data-only model; demand calculation logic lives in garbage-service business services.
 * - Field names must align with the billing/demand API schema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Demand {

    @JsonProperty("id")
    @CustomSafeHtml
    private String id;

    @JsonProperty("tenantId")
    @CustomSafeHtml
    private String tenantId;

    @JsonProperty("consumerCode")
    @CustomSafeHtml
    private String consumerCode;

    @JsonProperty("consumerType")
    @CustomSafeHtml
    private String consumerType;

    @JsonProperty("businessService")
    @CustomSafeHtml
    private String businessService;

    //    @Valid
    @JsonProperty("payer")
    private User payer;

    @JsonProperty("taxPeriodFrom")
    private Long taxPeriodFrom;

    @JsonProperty("taxPeriodTo")
    private Long taxPeriodTo;

    @Builder.Default
    @JsonProperty("demandDetails")
//    @Valid
    private List<DemandDetail> demandDetails = new ArrayList<>();

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("fixedBillExpiryDate")
    private Long fixedBillExpiryDate;

    @JsonProperty("billExpiryTime")
    private Long billExpiryTime;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @Builder.Default
    @JsonProperty("minimumAmountPayable")
    private BigDecimal minimumAmountPayable = BigDecimal.ZERO;

    @JsonProperty("status")
    private StatusEnum status;

    /**
     * Gets or Sets status
     */
    public enum StatusEnum {

        ACTIVE("ACTIVE"),

        CANCELLED("CANCELLED"),

        ADJUSTED("ADJUSTED"),

        EXPIRED("EXPIRED"),

        PAID("PAID");

        private String value;

        /**
         * Constructs the StatusEnum with the specified string value.
         *
         * @param value the string representation of the demand status
         */
        StatusEnum(String value) {
            this.value = value;
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
                if (String.valueOf(b.value).equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }

        /**
         * Returns the string representation of the demand status.
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