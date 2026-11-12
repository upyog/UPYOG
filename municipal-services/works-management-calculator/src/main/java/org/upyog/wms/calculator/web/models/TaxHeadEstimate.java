package org.upyog.wms.calculator.web.models;

import java.math.BigDecimal;

public record TaxHeadEstimate(String taxHeadCode, BigDecimal estimateAmount) {
}
