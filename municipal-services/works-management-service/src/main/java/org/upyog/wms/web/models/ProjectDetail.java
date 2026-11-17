package org.upyog.wms.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Size(max = 64)
    private String actionPlanId;

    @Pattern(regexp = "^[A-Za-z0-9]{1,12}$", message = "projectId must be alphanumeric, up to 12 characters")
    private String projectId;

    @Size(max = 256)
    private String projectName;

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
