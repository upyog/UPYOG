package org.egov.garbageservice.contract.bill;

//import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.User;
import org.egov.garbageservice.model.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contract model for a revenue demand (assessment) before or alongside bill generation.
 *
 * Behavior:
 * - Identifies the consumer (consumerCode, tenantId, businessService) and tax period
 *   (taxPeriodFrom, taxPeriodTo).
 * - Holds payer {@link org.egov.common.contract.request.User}, status via {@link StatusEnum},
 *   and a list of {@link DemandDetail} tax-head amounts.
 * - Supports bill expiry hints (fixedBillExpiryDate, billExpiryTime) and minimumAmountPayable.
 * - {@link #addDemandDetailsItem(DemandDetail)} appends a tax-head line to demandDetails.
 * - Maps to/from JSON for demand create, update, and search APIs.
 *
 * Notes:
 * - Used with {@link DemandRepository} when garbage-service creates or updates demands on billing service.
 * - Data-only model; demand calculation logic lives in garbage-service business services.
 * - Field names must align with the billing/demand API schema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Demand   {

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

        StatusEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static StatusEnum fromValue(String text) {
            for (StatusEnum b : StatusEnum.values()) {
                if (String.valueOf(b.value).equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("status")
    private StatusEnum status;


    public Demand addDemandDetailsItem(DemandDetail demandDetailsItem) {
        this.demandDetails.add(demandDetailsItem);
        return this;
    }

}