package org.upyog.wms.web.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Valid
    private List<Component> components;

    private AuditDetails auditDetails;
}
