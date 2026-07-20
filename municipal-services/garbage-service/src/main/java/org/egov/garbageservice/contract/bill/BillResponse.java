package org.egov.garbageservice.contract.bill;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for the billing/collection service response when fetching or updating bills.
 *
 * Behavior:
 * - Carries standard eGov {@link org.egov.common.contract.response.ResponseInfo} metadata.
 * - Holds a list of {@link Bill} objects returned by fetch, search, or update bill APIs.
 * - Deserialized from REST JSON via Jackson in {@link BillRepository}.
 *
 * Notes:
 * - JSON property for response info is spelled {@code ResposneInfo} (legacy typo in contract).
 * - Bill list key in JSON is {@code Bill}; must match the remote API for parsing to succeed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponse {

	@JsonProperty("ResposneInfo")
	private ResponseInfo resposneInfo = null;

	@JsonProperty("Bill")
	private List<Bill> bill = new ArrayList<>();

}
