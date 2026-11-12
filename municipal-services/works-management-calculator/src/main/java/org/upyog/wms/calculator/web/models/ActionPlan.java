package org.upyog.wms.calculator.web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Read-only view of an Action Plan (subset of the works-management-service contract). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionPlan {

    private String id;

    private String tenantId;

    private String applicationNumber;

    private String actionPlanNumber;

    private Integer version;

    private String financialYear;

    private String projectId;

    private String projectName;

    private String schemeCode;

    private BigDecimal approvedAmount;

    private String fundSource;

    private String status;

    private List<FundDetail> fundDetails;

    private List<Component> components;

    private AuditDetails auditDetails;
}
