package org.egov.ptr.models.oldProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import org.egov.ptr.models.enums.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxHeadEstimate {

    private String taxHeadCode;

    private BigDecimal estimateAmount;

    private Category category;
}
