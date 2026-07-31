package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.annotations.CustomSafeHtml;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * Request for dashboard-style aggregate counts of garbage applications by status.
 * Posted to /garbage-accounts/_counts with tenantId and RequestInfo.
 */
@Builder
public class TotalCountRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @CustomSafeHtml
    private String tenantId;

}
