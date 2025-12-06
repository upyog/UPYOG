package org.egov.dx.gis.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @NotNull
    @JsonProperty("tenantId")
    private String tenantId;

    @NotNull
    @JsonProperty("businessService")
    private String businessService; // e.g., "PT", "TL", "BP", etc.

    @JsonProperty("filters")
    private Map<String, Object> filters;

    @JsonProperty("geometryType")
    private String geometryType; // "point" or "polygon"

    @JsonProperty("includeBillData")
    private Boolean includeBillData; // whether to include billing information
    
    // Financial year parameters
    @JsonProperty("fromDate")
    private Long fromDate; // Financial year start timestamp

    @JsonProperty("toDate")
    private Long toDate;   // Financial year end timestamp
}
