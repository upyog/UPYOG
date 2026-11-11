package org.upyog.wms.web.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Constituent work of the project. componentNumber is stable across plan versions. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Component {

    @Size(max = 64)
    private String id;

    @Size(max = 64)
    private String tenantId;

    @Size(max = 64)
    private String componentNumber;

    @Size(max = 256)
    private String componentName;

    @Size(max = 64)
    private String category;

    @Size(max = 64)
    private String subCategory;

    @Size(max = 32)
    private String fundSource;

    @DecimalMin(value = "0.00", message = "approvedAmount must not be negative")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal approvedAmount;

    /** ACTIVE, or INACTIVE when removed in a revision (kept for history). */
    @Size(max = 32)
    private String status;

    private Boolean isActive;

    private Object additionalDetails;

    @Valid
    private List<FundDetail> fundDetails;

    @Valid
    private List<Beneficiary> beneficiaries;

    @Valid
    private List<Document> documents;

    private AuditDetails auditDetails;
}
