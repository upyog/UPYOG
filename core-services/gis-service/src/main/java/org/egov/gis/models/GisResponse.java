package org.egov.gis.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

/**
 * Response model for any municipal service GIS operations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GisResponse {

    private ResponseInfo responseInfo;
    private String tenantId;
    private String businessService;
    private Integer totalCount;
    private List<Entity> entities;
}
