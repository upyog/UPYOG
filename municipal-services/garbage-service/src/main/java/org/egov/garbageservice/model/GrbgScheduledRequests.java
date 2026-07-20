package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
/**
 * Audit of scheduled job invocations (bill generation, penalty) for garbage-service.
 * Tracks request payload references and execution status for ops troubleshooting.
 */
@NoArgsConstructor
public class GrbgScheduledRequests {

    @CustomSafeHtml
    private String uuid;
    private Long garbageId;
    @CustomSafeHtml
    private String type;
    private Long startDate;
    private Long endDate;
    private Boolean isActive;
}
