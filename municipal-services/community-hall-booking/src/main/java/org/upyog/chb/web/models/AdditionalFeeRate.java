package org.upyog.chb.web.models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class AdditionalFeeRate {

    private String feeType;
    private BigDecimal rate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal flatAmount;
    private BigDecimal amount;
    private String fromFY;
    private String applicableAfterDays;
    private Object active; // can be Boolean or String
    private boolean taxApplicable;
    private boolean refundable;
}