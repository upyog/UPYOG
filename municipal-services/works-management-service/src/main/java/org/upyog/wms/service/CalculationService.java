package org.upyog.wms.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.upyog.wms.config.WmsConfiguration;
import org.upyog.wms.repository.ServiceRequestRepository;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.calculation.CalculationCriteria;
import org.upyog.wms.web.models.calculation.CalculationReq;

/**
 * Calls works-management-calculator /_calculate on submission (DIGIT pattern: the plan is sent inside the
 * criteria, so the calculator does not depend on the asynchronous persister). Off by default: phase 1
 * has no fee.
 */
@Service
@RequiredArgsConstructor
public class CalculationService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final WmsConfiguration config;

    public void calculate(RequestInfo requestInfo, ActionPlan plan) {
        if (!Boolean.TRUE.equals(config.getCalculatorEnabled())) {
            return;
        }
        CalculationReq request = CalculationReq.builder()
                .requestInfo(requestInfo)
                .calculationCriteria(List.of(CalculationCriteria.builder()
                        .tenantId(plan.getTenantId())
                        .applicationNumber(plan.getApplicationNumber())
                        .actionPlan(plan)
                        .build()))
                .build();
        serviceRequestRepository.post(config.getCalculatorHost() + config.getCalculatorCalculatePath(), request,
                Object.class);
    }
}
