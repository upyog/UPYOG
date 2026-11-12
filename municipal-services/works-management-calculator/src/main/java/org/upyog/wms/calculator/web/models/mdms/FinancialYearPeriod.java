package org.upyog.wms.calculator.web.models.mdms;

/** MDMS WorksManagement.FinancialYear with its period (epoch millis), used as the demand tax period. */
public record FinancialYearPeriod(String code, Long startingDate, Long endingDate) {
}
