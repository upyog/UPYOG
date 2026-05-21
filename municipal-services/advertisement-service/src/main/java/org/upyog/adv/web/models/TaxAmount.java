package org.upyog.adv.web.models;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
    public class TaxAmount {
    private String applicationType;
    private String serviceType;
    private String feeType;
    private BigDecimal rate;
    }
