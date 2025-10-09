package org.egov.dx.gis.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.common.contract.response.ResponseInfo;

/**
 * GeoJSON response for any municipal service
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoJsonResponse {

    private ResponseInfo responseInfo;
    private String tenantId;
    private String businessService;
    private Integer totalCount;
    private Object geoJsonData;
}
