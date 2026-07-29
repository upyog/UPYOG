package org.egov.garbageservice.model;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmountCalculationResult {
    private BigDecimal totalAmount;
    private BigDecimal rebateAmount;
    private BigDecimal payableAmount;
}
