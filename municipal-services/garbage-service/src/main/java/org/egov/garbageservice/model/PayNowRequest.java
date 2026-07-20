package org.egov.garbageservice.model;

import java.util.List;

import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * Citizen pay-now request to fetch bill details and initiate payment for a garbage application.
 * Posted to /garbage-accounts/_payNow and processed by GarbageAccountService.
 */
@Builder
public class PayNowRequest {

	@CustomSafeHtml
	private String userUuid;

	private List<String> garbageApplicationNumbers;

	private List<String> garbageUuid;

	private List<String> billStatus;

	@CustomSafeHtml
	private String month;

	@CustomSafeHtml
	private String year;

	private List<String> propertyIds;
	
	private List<Long> garbageIds;

	@Builder.Default
	private Boolean isEmptyBillFilter = false;

}
