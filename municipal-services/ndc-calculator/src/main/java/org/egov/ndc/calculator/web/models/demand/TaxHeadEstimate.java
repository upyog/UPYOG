package org.egov.ndc.calculator.web.models.demand;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.ndc.calculator.web.models.bill.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxHeadEstimate {


    private String taxHeadCode;

    private BigDecimal estimateAmount;

    private Category category;
}
