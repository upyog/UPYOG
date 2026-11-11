package org.upyog.wms.web.models;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Section 1 project details. Kept as a list item on ActionPlan so 1:1 can become 1:many later. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetail {

    @Size(max = 64)
    private String id;

    @Size(max = 64)
    private String tenantId;

    @Size(max = 64)
    private String purposeCode;

    @Size(max = 256)
    private String purposeOther;

    private Long startDate;

    private Long endDate;

    @Size(max = 64)
    private String typeOfWork;

    @Size(max = 64)
    private String workCategory;

    private Boolean isActive;

    private Object additionalDetails;

    private AuditDetails auditDetails;
}
