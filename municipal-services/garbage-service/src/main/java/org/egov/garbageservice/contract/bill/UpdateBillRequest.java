package org.egov.garbageservice.contract.bill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

/**
 * REST request body for calling the billing service bill update/cancel API.
 * <p>
 * Behavior:
 * - Bundles {@link org.egov.common.contract.request.RequestInfo} with {@link UpdateBillCriteria}.
 * - Serialized and posted by {@link BillRepository#cancelBill(UpdateBillCriteria, org.egov.common.contract.request.RequestInfo)}.
 * <p>
 * Notes:
 * - JSON property names use PascalCase ({@code RequestInfo}, {@code UpdateBillCriteria}) per billing contract.
 * - Field names in Java match JSON keys for Jackson mapping.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBillRequest {

    @NotNull
    private RequestInfo RequestInfo;

    @NotNull
    @Valid
    private UpdateBillCriteria UpdateBillCriteria;
}
