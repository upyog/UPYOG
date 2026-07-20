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
 * Collection route or zone metadata associated with garbage pickup operations.
 * Used in common/master configuration alongside collection staff assignments.
 */
@NoArgsConstructor
public class GrbgCollection {

    @CustomSafeHtml
    private String uuid;
    private Long garbageId;
    @CustomSafeHtml
    private String staffUuid;
    @CustomSafeHtml
    private String collecType;
    private Long startDate;
    private Long endDate;
    private Boolean isActive;
    @CustomSafeHtml
    private String createdBy;
    private Long createdDate;
    @CustomSafeHtml
    private String lastModifiedBy;
    private Long lastModifiedDate;
}
