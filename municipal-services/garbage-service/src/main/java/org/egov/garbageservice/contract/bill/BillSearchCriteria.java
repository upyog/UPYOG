package org.egov.garbageservice.contract.bill;

import java.util.Set;

import jakarta.validation.constraints.Pattern;

import org.egov.garbageservice.contract.bill.Demand.StatusEnum;

//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Pattern;
//import jakarta.validation.constraints.Size;

//import org.egov.tl.web.models.collection.Bill.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.egov.tracer.annotations.CustomSafeHtml;

/**
 * Query filters used when garbage-service searches bills on the billing/collection service.
 *
 * Behavior:
 * - Built in services (e.g. GarbageAccountService, GarbageBillService) with tenantId, consumerCode,
 *   billId, demandId, status, period range, and pagination (offset, size).
 * - Passed to {@link BillRepository#searchBill(BillSearchCriteria, org.egov.common.contract.request.RequestInfo)}
 *   which builds the search URL query parameters.
 * - Supports flags such as retrieveAll, retrieveOldest, isActive, and isCancelled.
 *
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
//	@Size(max = 256)
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

//	@Size(max = 256)
	@CustomSafeHtml
	private String billNumber;

//	@Size(max = 256)
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
