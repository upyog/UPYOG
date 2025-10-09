package org.egov.dx.gis.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.common.contract.request.RequestInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * GeoJSON request for any municipal service
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoJsonRequest {

    @NotNull
    @Valid
    private RequestInfo requestInfo;

    @NotNull
    private String tenantId;

    @NotNull
    private String businessService; // e.g., "PT", "TL", "BP", etc.

    private Map<String, Object> filters;
    private String geometryType; // "point" or "polygon"
    private Boolean includeBillData; // whether to include billing information
    
    // Financial year parameters (passed from UI)
    private Long fromDate; // Financial year start timestamp
    private Long toDate;   // Financial year end timestamp
}
