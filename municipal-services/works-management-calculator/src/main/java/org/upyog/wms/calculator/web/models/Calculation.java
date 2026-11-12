package org.upyog.wms.calculator.web.models;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calculation {

    private String tenantId;

    private String applicationNumber;

    private FinancialSummary financialSummary;

    private List<TaxHeadEstimate> taxHeadEstimates;

    /** Total fee (0 when no fee is configured). */
    private BigDecimal totalAmount;

    /** Set when a demand was created / updated in billing-service. */
    private String demandId;
}
