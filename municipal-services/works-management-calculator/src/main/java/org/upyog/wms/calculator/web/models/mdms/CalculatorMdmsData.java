package org.upyog.wms.calculator.web.models.mdms;

import java.util.List;
import java.util.Map;

public record CalculatorMdmsData(List<FeeConfig> fees, Map<String, FinancialYearPeriod> financialYears) {
}
