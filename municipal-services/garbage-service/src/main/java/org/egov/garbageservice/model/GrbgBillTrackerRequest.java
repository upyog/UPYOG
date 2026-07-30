package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

/**
 * API request to search or persist GrbgBillTracker records with RequestInfo and criteria.
 * Supports bill tracker queries from scheduler and admin flows.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrbgBillTrackerRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("grbgBillTracker")
    private GrbgBillTracker grbgBillTracker;
}
