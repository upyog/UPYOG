package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;
import org.egov.common.contract.request.RequestInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
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
 * Request for fee calculation or workflow action lookup on garbage applications.
 * Filters by application numbers, property ids, bill status, period, and garbage UUIDs.
 * Used by GarbageAccountController /fetch/{CALCULATEFEE|ACTIONS} endpoints.
 */
@Builder
public class GarbageAccountActionRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;

	private List<String> applicationNumbers;

	private List<String> billStatus;

	@CustomSafeHtml
	private String month;

	@CustomSafeHtml
	private String year;

	private List<String> propertyIds;
	
	private List<String> garbageUuid;
	
	@Builder.Default
	private Boolean skipValidation = false;

	@Builder.Default
	private Boolean isEmptyBillFilter = false;

}
