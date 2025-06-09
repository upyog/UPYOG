package org.egov.pt.models.bill;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.collection.Bill;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BillRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("Bills")
	@Default
	private List<Bill> bills = new ArrayList<>();
}
