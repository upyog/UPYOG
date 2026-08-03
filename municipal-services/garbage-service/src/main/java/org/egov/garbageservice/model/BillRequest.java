package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.contract.bill.Bill;

import java.util.ArrayList;
import java.util.List;

/**
 * Request body for posting bill updates to the billing/collection service from garbage-service.
 * Wraps RequestInfo and a list of contract Bill objects.
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
