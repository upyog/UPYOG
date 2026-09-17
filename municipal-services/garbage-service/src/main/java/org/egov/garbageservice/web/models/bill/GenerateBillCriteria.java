package org.egov.garbageservice.web.models.bill;


import lombok.*;
import org.egov.tracer.annotations.CustomSafeHtml;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
/**
 * Request parameters for generating or fetching a bill for a garbage consumer.
 *
 * Behavior:
 * - Supplies tenantId, consumerCode, and businessService required by the billing service.
 * - Optionally narrows the request with demandId, mobileNumber, or email.
 * - Used by {@link BillRepository#fetchBill(GenerateBillCriteria, org.egov.common.contract.request.RequestInfo)}
 *   to build the fetch-bill REST query string.
 *
 * Notes:
 * - Built in GarbageAccountService when a bill must be generated or retrieved for an account.
 * - All string fields use {@code @CustomSafeHtml} for tracer sanitization.
 * - Endpoint and host come from {@link org.egov.garbageservice.util.GrbgConstants}, not this class.
 */
public class GenerateBillCriteria {

    //    @NotNull
    @CustomSafeHtml
    private String tenantId;

    @CustomSafeHtml
    private String demandId;

    //    @NotNull
    @CustomSafeHtml
    private String consumerCode;

    @CustomSafeHtml
    private String businessService;

    //    @Email
    @CustomSafeHtml
    private String email;

    @CustomSafeHtml
    private String mobileNumber;

}