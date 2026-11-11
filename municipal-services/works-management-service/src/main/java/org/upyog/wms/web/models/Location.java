package org.upyog.wms.web.models;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Size(max = 64)
    private String id;

    @Size(max = 64)
    private String tenantId;

    @Size(max = 64)
    private String stateCode;

    @Size(max = 64)
    private String districtCode;

    @Size(max = 256)
    private String mlaConstituency;

    @Size(max = 256)
    private String mpConstituency;

    private Boolean isActive;

    private Object additionalDetails;

    private AuditDetails auditDetails;
}
