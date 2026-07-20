package org.egov.garbageservice.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
/**
 * Filter criteria for garbage bill search by ids, bill ref, garbage id, payment id, or payment status.
 * Used inside SearchGarbageBillRequest when querying persisted GarbageBill rows.
 */
@NoArgsConstructor
public class GarbageBillSearchCriteria {

	private List<Long> ids;

	private List<String> billRefNos;

	private List<Long> garbageIds;

	private List<String> paymentIds;

	private List<String> paymentStatus;
}
