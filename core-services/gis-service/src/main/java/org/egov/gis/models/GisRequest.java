package org.egov.gis.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.common.contract.request.RequestInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Request model for any municipal service GIS operations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GisRequest {

    @NotNull
    @Valid
    private RequestInfo requestInfo;

    @NotNull
    private String tenantId;

    @NotNull
    private String businessService; // e.g., "PT", "TL", "BP", etc.

    private Map<String, Object> searchCriteria;
    private List<String> entityIds;
    private String geometryType; // "point" or "polygon"
    
    // Financial year parameters
    private Long fromDate;
    private Long toDate;
}
