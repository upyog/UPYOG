package org.upyog.wms.calculator.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.upyog.wms.calculator.config.CalculatorConfiguration;
import org.upyog.wms.calculator.repository.ServiceRequestRepository;
import org.upyog.wms.calculator.web.models.ActionPlan;
import org.upyog.wms.calculator.web.models.ActionPlanResponse;
import org.upyog.wms.calculator.web.models.RequestInfoWrapper;

/** Reads Action Plans from works-management-service (one call for many application numbers). */
@Service
@RequiredArgsConstructor
public class ActionPlanFetchService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CalculatorConfiguration config;

    public Map<String, ActionPlan> fetch(RequestInfo requestInfo, String tenantId, List<String> applicationNumbers) {
        if (applicationNumbers.isEmpty()) {
            return Map.of();
        }
        String url = config.getWmsHost() + config.getWmsSearchPath()
                + "?tenantId=" + encode(tenantId)
                + "&applicationNumbers=" + encode(String.join(",", applicationNumbers))
                + "&limit=" + applicationNumbers.size();
        ActionPlanResponse response = serviceRequestRepository.post(url,
                RequestInfoWrapper.builder().requestInfo(requestInfo).build(), ActionPlanResponse.class);
        List<ActionPlan> plans = response == null || response.getActionPlans() == null ? List.of()
                : response.getActionPlans();
        return plans.stream().collect(Collectors.toMap(ActionPlan::getApplicationNumber, Function.identity()));
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
