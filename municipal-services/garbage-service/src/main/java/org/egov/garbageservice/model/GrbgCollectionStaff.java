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
 * Staff member assigned to a garbage collection unit or route.
 * Links employee identifiers to operational collection configuration.
 */
@NoArgsConstructor
public class GrbgCollectionStaff {

    @CustomSafeHtml
    private String uuid;
    @CustomSafeHtml
    private String grbgCollectionUnitUuid;
    @CustomSafeHtml
    private String employeeId;
    @CustomSafeHtml
    private String role;
    private Boolean isActive;
}
