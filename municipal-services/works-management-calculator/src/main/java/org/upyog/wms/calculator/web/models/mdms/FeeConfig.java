package org.upyog.wms.calculator.web.models.mdms;

import java.math.BigDecimal;
import java.util.List;

/**
 * MDMS WorksManagement.FeeConfig entry.
 * calculationType FLAT -> value is the fee; PERCENTAGE -> value is % of the approved plan amount.
 * applicableFundSources empty = all fund sources.
 */
public record FeeConfig(String code, String businessService, String taxHeadCode, String calculationType,
                        BigDecimal value, BigDecimal minAmount, BigDecimal maxAmount,
                        List<String> applicableFundSources, boolean active) {
}
