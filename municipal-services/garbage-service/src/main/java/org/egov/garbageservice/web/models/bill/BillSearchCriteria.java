package org.egov.garbageservice.web.models.bill;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.garbageservice.web.models.bill.Demand.StatusEnum;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.util.Set;

/**
 * Query filters used when garbage-service searches bills on the billing/collection service.
 * <p>
 * Behavior:
 * - Built in services (e.g. GarbageAccountService, GarbageBillService) with tenantId, consumerCode,
 * billId, demandId, status, period range, and pagination (offset, size).
 * - Passed to {@link BillRepository#searchBill(BillSearchCriteria, org.egov.common.contract.request.RequestInfo)}
 * which builds the search URL query parameters.
 * - Supports flags such as retrieveAll, retrieveOldest, isActive, and isCancelled.
 * <p>
 * Notes:
 * - Field set mirrors billing-service search API parameters; unused fields can be left null.
 * - {@code service} is typically set to GB (garbage business service) in repository calls.
 * - mobileNumber is validated as a 10-digit pattern when present.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillSearchCriteria {

    //	@NotNull
    @CustomSafeHtml
    private String tenantId;

    private Set<String> billId;

    private Long fromPeriod;

    private Long toPeriod;

    @Default
    private Boolean retrieveOldest = false;

    @Default
    private Boolean retrieveAll = false;

    private Boolean isActive;

    private Boolean isCancelled;

    private Set<String> consumerCode;

    private Set<String> demandId;

    @CustomSafeHtml
    private String billNumber;

    @CustomSafeHtml
    private String service;

    @Default
    private boolean isOrderBy = false;

    private Long size;

    private Long offset;

    //	@Email
    @CustomSafeHtml
    private String email;

    private StatusEnum status;

    @Pattern(regexp = "^[0-9]{10}$", message = "MobileNumber should be 10 digit number")
    @CustomSafeHtml
    private String mobileNumber;
}
