package org.upyog.wms.calculator.service;

import static org.upyog.wms.calculator.constants.CalculatorConstants.INVALID_CRITERIA;
import static org.upyog.wms.calculator.constants.CalculatorConstants.NOT_FOUND;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.wms.calculator.config.CalculatorConfiguration;
import org.upyog.wms.calculator.web.models.ActionPlan;
import org.upyog.wms.calculator.web.models.Calculation;
import org.upyog.wms.calculator.web.models.CalculationCriteria;
import org.upyog.wms.calculator.web.models.CalculationReq;
import org.upyog.wms.calculator.web.models.GetBillCriteria;
import org.upyog.wms.calculator.web.models.TaxHeadEstimate;
import org.upyog.wms.calculator.web.models.demand.Demand;
import org.upyog.wms.calculator.web.models.mdms.CalculatorMdmsData;

/**
 * _estimate      : summary + fees, no side effect (used by the UI "calculator" of the Financial Summary).
 * _calculate     : _estimate + create / update the billing demand when a fee is due and demands are enabled.
 * _updatedemand  : re-apply current fees to existing demands (e.g. after a revision or a fee change).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalculationService {

    private final EstimationService estimationService;
    private final MdmsService mdmsService;
    private final ActionPlanFetchService actionPlanFetchService;
    private final DemandService demandService;
    private final CalculatorConfiguration config;

    private record Item(CalculationCriteria criteria, ActionPlan plan) {
    }

    public List<Calculation> estimate(CalculationReq request) {
        return resolve(request).stream()
                .map(item -> calculation(request.getRequestInfo(), item.plan()))
                .toList();
    }

    public List<Calculation> calculate(CalculationReq request) {
        List<Calculation> calculations = new ArrayList<>();
        for (Item item : resolve(request)) {
            Calculation calculation = calculation(request.getRequestInfo(), item.plan());
            if (Boolean.TRUE.equals(config.getDemandEnabled())) {
                applyDemand(request.getRequestInfo(), item.plan(), calculation).ifPresent(d ->
                        calculation.setDemandId(d.getId()));
            }
            calculations.add(calculation);
        }
        return calculations;
    }

    public List<Demand> updateDemand(RequestInfo requestInfo, GetBillCriteria criteria) {
        Map<String, ActionPlan> plans = actionPlanFetchService.fetch(requestInfo, criteria.getTenantId(),
                criteria.getConsumerCodes());
        CalculatorMdmsData mdms = mdmsService.getMasterData(requestInfo, criteria.getTenantId());
        List<Demand> updated = new ArrayList<>();
        for (String consumerCode : criteria.getConsumerCodes()) {
            ActionPlan plan = Optional.ofNullable(plans.get(consumerCode)).orElseThrow(() ->
                    new CustomException(NOT_FOUND, "Action Plan not found for application " + consumerCode));
            List<TaxHeadEstimate> fees = estimationService.fees(plan, mdms.fees(), config.getBusinessService());
            demandService.searchActive(requestInfo, criteria.getTenantId(), consumerCode)
                    .flatMap(demand -> demandService.update(requestInfo, demand, fees))
                    .ifPresent(updated::add);
        }
        return updated;
    }

    private Calculation calculation(RequestInfo requestInfo, ActionPlan plan) {
        CalculatorMdmsData mdms = mdmsService.getMasterData(requestInfo, plan.getTenantId());
        List<TaxHeadEstimate> fees = estimationService.fees(plan, mdms.fees(), config.getBusinessService());
        return Calculation.builder()
                .tenantId(plan.getTenantId())
                .applicationNumber(plan.getApplicationNumber())
                .financialSummary(estimationService.summarize(plan))
                .taxHeadEstimates(fees)
                .totalAmount(fees.stream().map(TaxHeadEstimate::estimateAmount).reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();
    }

    private Optional<Demand> applyDemand(RequestInfo requestInfo, ActionPlan plan, Calculation calculation) {
        Optional<Demand> existing = demandService.searchActive(requestInfo, plan.getTenantId(),
                plan.getApplicationNumber());
        if (existing.isPresent()) {
            return demandService.update(requestInfo, existing.get(), calculation.getTaxHeadEstimates())
                    .or(() -> existing);
        }
        if (calculation.getTotalAmount().signum() == 0) {
            return Optional.empty();
        }
        CalculatorMdmsData mdms = mdmsService.getMasterData(requestInfo, plan.getTenantId());
        return Optional.of(demandService.create(requestInfo, plan, calculation.getTaxHeadEstimates(),
                mdms.financialYears().get(plan.getFinancialYear())));
    }

    /** Validates the criteria and loads plans that were given only by application number (one call). */
    private List<Item> resolve(CalculationReq request) {
        List<CalculationCriteria> criteria = request.getCalculationCriteria();
        Map<String, List<String>> toFetch = new LinkedHashMap<>();
        for (int i = 0; i < criteria.size(); i++) {
            CalculationCriteria c = criteria.get(i);
            if (c.getActionPlan() == null && (c.getApplicationNumber() == null || c.getApplicationNumber().isBlank())) {
                throw new CustomException(INVALID_CRITERIA, "CalculationCriteria[" + i
                        + "]: send actionPlan or applicationNumber");
            }
            if (c.getActionPlan() != null && c.getActionPlan().getTenantId() == null) {
                c.getActionPlan().setTenantId(c.getTenantId());
            }
            if (c.getActionPlan() == null) {
                toFetch.computeIfAbsent(c.getTenantId(), t -> new ArrayList<>()).add(c.getApplicationNumber());
            }
        }
        Map<String, ActionPlan> fetched = new LinkedHashMap<>();
        toFetch.forEach((tenant, numbers) -> fetched.putAll(actionPlanFetchService.fetch(request.getRequestInfo(),
                tenant, numbers)));

        return criteria.stream().map(c -> {
            ActionPlan plan = c.getActionPlan() != null ? c.getActionPlan() : fetched.get(c.getApplicationNumber());
            if (plan == null) {
                throw new CustomException(NOT_FOUND, "Action Plan not found for application " + c.getApplicationNumber());
            }
            return new Item(c, plan);
        }).toList();
    }
}
