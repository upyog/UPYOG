package org.egov.garbageservice.web.models.bill;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.garbageservice.web.models.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.math.BigDecimal;

/**
 * Contract model for one tax-head line on a {@link Demand}.
 * <p>
 * Behavior:
 * - Stores taxHeadMasterCode, taxAmount, and collectionAmount for a single levy.
 * - Links to parent demand via demandId and tenantId; includes audit and additionalDetails.
 * - Serialized as part of {@link Demand#getDemandDetails()} in demand create/update/search payloads.
 * <p>
 * Notes:
 * - Data-only model; tax computation happens upstream in garbage charge calculation services.
 * - collectionAmount defaults to zero when not set.
 * - Field names must align with the billing/demand API schema.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemandDetail {

    @JsonProperty("id")
    @CustomSafeHtml
    private String id;

    @JsonProperty("demandId")
    @CustomSafeHtml
    private String demandId;

    @JsonProperty("taxHeadMasterCode")
    @CustomSafeHtml
    private String taxHeadMasterCode;

    @JsonProperty("taxAmount")
    private BigDecimal taxAmount;

    @Default
    @JsonProperty("collectionAmount")
    private BigDecimal collectionAmount = BigDecimal.ZERO;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("tenantId")
    @CustomSafeHtml
    private String tenantId;


}

