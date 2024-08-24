package org.egov.garbageservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class GrbgScheduledRequests {

    private String uuid;
    private Long garbageId;
    private String type;
    private Long startDate;
    private Long endDate;
    private Boolean isActive;
}
