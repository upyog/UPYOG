package org.egov.garbageservice.model;

import lombok.*;

import java.math.BigDecimal;

/**
 * Encapsulates the financial calculation results for garbage user charges, including base fee (totalAmount), eligible rebate (rebateAmount), and net payable amount.
 */
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
