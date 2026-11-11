package org.upyog.wms.web.models;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A beneficiary. componentId == null means plan-level beneficiary. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {

    @Size(max = 64)
    private String id;

    @Size(max = 64)
    private String tenantId;

    @Size(max = 64)
    private String actionPlanId;

    @Size(max = 64)
    private String componentId;

    @Size(max = 256)
    private String name;

    @Size(max = 1024)
    private String remark;

    private Boolean isActive;

    private Object additionalDetails;

    private AuditDetails auditDetails;
}
