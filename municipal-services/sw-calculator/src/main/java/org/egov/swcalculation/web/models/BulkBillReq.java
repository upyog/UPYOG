package org.egov.swcalculation.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.swcalculation.web.models.BulkBillCriteria;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BulkBillReq {

	@JsonProperty("RequestInfo")
	@NotNull
	private RequestInfo requestInfo;

	@Valid
	@NotNull
	@JsonProperty("BulkBillCriteria")
	private BulkBillCriteria bulkBillCriteria;

}