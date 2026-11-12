package org.upyog.wms.calculator.service;

import static org.upyog.wms.calculator.constants.CalculatorConstants.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.upyog.wms.calculator.web.models.ActionPlan;
import org.upyog.wms.calculator.web.models.Component;
import org.upyog.wms.calculator.web.models.FinancialSummary;
import org.upyog.wms.calculator.web.models.FinancialSummary.CategoryBreakup;
import org.upyog.wms.calculator.web.models.FinancialSummary.ComponentBreakup;
import org.upyog.wms.calculator.web.models.FinancialSummary.FundBreakup;
import org.upyog.wms.calculator.web.models.FundDetail;
import org.upyog.wms.calculator.web.models.TaxHeadEstimate;
import org.upyog.wms.calculator.web.models.mdms.FeeConfig;

/**
 * Pure calculations (no I/O): the Financial Summary of FRS Section 5 and the configured fees.
 * Amounts use BigDecimal with 2 decimals, HALF_UP; percentages are of the Total Approved Amount.
 */
@Service
public class EstimationService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public FinancialSummary summarize(ActionPlan plan) {
        BigDecimal total = scale(plan.getApprovedAmount());
        List<Component> active = list(plan.getComponents()).stream()
                .filter(c -> !Boolean.FALSE.equals(c.getIsActive()) && !COMPONENT_INACTIVE.equals(c.getStatus()))
                .toList();

        List<ComponentBreakup> components = active.stream()
                .map(c -> new ComponentBreakup(c.getComponentNumber(), c.getComponentName(), c.getCategory(),
                        scale(c.getApprovedAmount()), percent(c.getApprovedAmount(), total)))
                .toList();
        BigDecimal componentTotal = sum(active, Component::getApprovedAmount);

        Map<String, BigDecimal> byCategory = active.stream().collect(Collectors.groupingBy(
                c -> Objects.requireNonNullElse(c.getCategory(), "UNSPECIFIED"), LinkedHashMap::new,
                Collectors.reducing(BigDecimal.ZERO, c -> nz(c.getApprovedAmount()), BigDecimal::add)));
        List<CategoryBreakup> categories = byCategory.entrySet().stream()
                .map(e -> new CategoryBreakup(e.getKey(), scale(e.getValue()), percent(e.getValue(), total)))
                .toList();

        List<FundDetail> planFunds = live(plan.getFundDetails());
        List<FundBreakup> planFundBreakup = fundBreakup(planFunds, f -> f.getFundType() + "|" + f.getSourceCode(), total);
        List<FundDetail> componentFunds = active.stream().flatMap(c -> live(c.getFundDetails()).stream()).toList();
        List<FundBreakup> componentFundBreakup = fundBreakup(componentFunds, f -> f.getFundType() + "|", total);

        BigDecimal unallocated = total.subtract(componentTotal);
        List<String> observations = new ArrayList<>();
        if (unallocated.signum() > 0) {
            observations.add("Unallocated balance of " + unallocated.toPlainString()
                    + " - component amounts are less than the Total Approved Amount");
        } else if (unallocated.signum() < 0) {
            observations.add("Components exceed the Total Approved Amount by " + unallocated.negate().toPlainString());
        }
        BigDecimal planFundTotal = sum(planFunds, FundDetail::getAmount);
        if (planFundTotal.compareTo(total) != 0) {
            observations.add("Fund break-up total " + planFundTotal.toPlainString()
                    + " does not equal the Total Approved Amount " + total.toPlainString());
        }
        for (String type : List.of(FUND_GOVT_GRANT, FUND_OSR)) {
            BigDecimal p = sum(planFunds.stream().filter(f -> type.equals(f.getFundType())).toList(), FundDetail::getAmount);
            BigDecimal c = sum(componentFunds.stream().filter(f -> type.equals(f.getFundType())).toList(), FundDetail::getAmount);
            if (p.compareTo(c) != 0) {
                observations.add(type + " funding of components (" + c.toPlainString()
                        + ") differs from the plan (" + p.toPlainString() + ")");
            }
        }
        return new FinancialSummary(total, componentTotal, unallocated, observations.isEmpty(), components,
                categories, planFundBreakup, componentFundBreakup, observations);
    }

    /** Fees from MDMS FeeConfig (FLAT or PERCENTAGE of the approved amount, clamped to min / max). */
    public List<TaxHeadEstimate> fees(ActionPlan plan, List<FeeConfig> configs, String businessService) {
        BigDecimal base = scale(plan.getApprovedAmount());
        return configs.stream()
                .filter(FeeConfig::active)
                .filter(f -> businessService.equals(f.businessService()))
                .filter(f -> f.applicableFundSources() == null || f.applicableFundSources().isEmpty()
                        || f.applicableFundSources().contains(plan.getFundSource()))
                .map(f -> new TaxHeadEstimate(f.taxHeadCode(), clamp(fee(f, base), f)))
                .filter(t -> t.estimateAmount().signum() > 0)
                .sorted(Comparator.comparing(TaxHeadEstimate::taxHeadCode))
                .toList();
    }

    private static BigDecimal fee(FeeConfig f, BigDecimal base) {
        BigDecimal value = nz(f.value());
        return switch (f.calculationType() == null ? "" : f.calculationType()) {
            case CALCULATION_FLAT -> scale(value);
            case CALCULATION_PERCENTAGE -> base.multiply(value).divide(HUNDRED, 2, RoundingMode.HALF_UP);
            default -> BigDecimal.ZERO;
        };
    }

    private static BigDecimal clamp(BigDecimal amount, FeeConfig f) {
        BigDecimal result = amount;
        if (f.minAmount() != null && result.compareTo(f.minAmount()) < 0) {
            result = f.minAmount();
        }
        if (f.maxAmount() != null && result.compareTo(f.maxAmount()) > 0) {
            result = f.maxAmount();
        }
        return scale(result);
    }

    private static List<FundBreakup> fundBreakup(List<FundDetail> rows, Function<FundDetail, String> key,
                                                 BigDecimal total) {
        Map<String, BigDecimal> grouped = rows.stream().collect(Collectors.groupingBy(key, LinkedHashMap::new,
                Collectors.reducing(BigDecimal.ZERO, f -> nz(f.getAmount()), BigDecimal::add)));
        return grouped.entrySet().stream().map(e -> {
            String[] parts = e.getKey().split("\\|", -1);
            String source = parts.length > 1 && !parts[1].isEmpty() && !"null".equals(parts[1]) ? parts[1] : null;
            return new FundBreakup(parts[0], source, scale(e.getValue()), percent(e.getValue(), total));
        }).toList();
    }

    private static BigDecimal percent(BigDecimal amount, BigDecimal total) {
        if (amount == null || total == null || total.signum() == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return amount.multiply(HUNDRED).divide(total, 2, RoundingMode.HALF_UP);
    }

    private static <T> BigDecimal sum(List<T> items, Function<T, BigDecimal> amount) {
        return scale(items.stream().map(amount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private static List<FundDetail> live(List<FundDetail> rows) {
        return list(rows).stream().filter(f -> !Boolean.FALSE.equals(f.getIsActive())).toList();
    }

    private static <T> List<T> list(List<T> items) {
        return items == null ? List.of() : items;
    }

    private static BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static BigDecimal scale(BigDecimal value) {
        return nz(value).setScale(2, RoundingMode.HALF_UP);
    }
}
