package org.upyog.wms.web.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A funding row. componentId == null means plan-level funding. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundDetail {

    @Size(max = 64)
    private String id;

    @Size(max = 64)
    private String tenantId;

    @Size(max = 64)
    private String actionPlanId;

    @Size(max = 64)
    private String componentId;

    /** GOVT_GRANT or OSR. */
    @Size(max = 32)
    private String fundType;

    /** MDMS GovernmentGrant code (for GOVT_GRANT) or OwnSourceRevenue code (for OSR). */
    @Size(max = 64)
    private String sourceCode;

    @Size(max = 256)
    private String fundName;

    @DecimalMin(value = "0.00", message = "amount must not be negative")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal amount;

    private Boolean isActive;

    private Object additionalDetails;

    private AuditDetails auditDetails;
}
