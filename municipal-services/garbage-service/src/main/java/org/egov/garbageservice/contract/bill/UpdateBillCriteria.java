package org.egov.garbageservice.contract.bill;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.garbageservice.contract.bill.Demand.StatusEnum;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.util.Set;

/**
 * Criteria payload for updating bill status on the billing/collection service (e.g. cancel).
 * <p>
 * Behavior:
 * - Identifies targets by tenantId, businessService, and consumerCodes (and optional billIds).
 * - Specifies the new status via {@link Demand.StatusEnum} statusToBeUpdated.
 * - Carries additionalDetails JSON for extra context sent to the billing API.
 * - Wrapped inside {@link UpdateBillRequest} for POST body to the update/cancel bill endpoint.
 * <p>
 * Notes:
 * - Used from {@link BillRepository#cancelBill(UpdateBillCriteria, org.egov.common.contract.request.RequestInfo)}.
 * - Mandatory fields are enforced with {@code @NotNull} for API validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBillCriteria {

    @NotNull
    @CustomSafeHtml
    private String tenantId;

    @NotNull
    private Set<String> consumerCodes;

    @NotNull
    @CustomSafeHtml
    private String businessService;

    @NotNull
    private JsonNode additionalDetails;

    private Set<String> billIds;

    @NotNull
    private StatusEnum statusToBeUpdated;
}
