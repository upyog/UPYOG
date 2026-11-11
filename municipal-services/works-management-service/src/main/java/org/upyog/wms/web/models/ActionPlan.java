package org.upyog.wms.web.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Action Plan header with all sections. One object = one version of the plan.
 * Bean validation here covers only rules that hold even for a draft (sizes, formats, no negatives).
 * Mandatory / conditional / reconciliation rules run on submission (see SubmissionRules).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPlan {

    @Size(max = 64)
    private String id;

    @Size(max = 64)
    private String tenantId;

    @Size(max = 64)
    private String applicationNumber;

    /** Set by the system on approval. Send it on _create to start a revision of an approved plan. */
    @Size(max = 64)
    private String actionPlanNumber;

    private Integer version;

    private Boolean isLatestVersion;

    @Size(max = 16)
    private String financialYear;

    @Pattern(regexp = "^[A-Za-z0-9]{1,12}$", message = "projectId must be alphanumeric, up to 12 characters")
    private String projectId;

    @Size(max = 256)
    private String projectName;

    private Boolean adminSanction;

    private Boolean technicalSanction;

    @Valid
    private List<ProjectDetail> projectDetails;

    @Valid
    private List<Recommendation> recommendations;

    @Valid
    private List<Location> locations;

    /** Plan-level beneficiaries ("Beneficiary Category = Yes"). */
    @Valid
    private List<Beneficiary> beneficiaries;

    private Long offlineApprovalDate;

    @Size(max = 256)
    private String approvingAuthority;

    @Size(max = 64)
    private String schemeCode;

    @Size(max = 64)
    private String subSchemeCode;

    @DecimalMin(value = "0.00", message = "approvedAmount must not be negative")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal approvedAmount;

    @Size(max = 32)
    private String fundSource;

    /** Plan-level funding rows (componentId is null). */
    @Valid
    private List<FundDetail> fundDetails;

    @Min(0)
    @Max(10000)
    private Integer totalComponents;

    @Valid
    private List<Component> components;

    @Valid
    private List<Document> documents;

    private Boolean declarationAccepted;

    private String status;

    private Long submittedDate;

    private Long approvedDate;

    private String approvedBy;

    private Boolean isActive;

    private Object additionalDetails;

    private AuditDetails auditDetails;

    /** Section 7 / workflow input. Not persisted by this service. */
    @Valid
    private Workflow workflow;
}
