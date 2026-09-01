package org.upyog.dashboard.transformer.impl;

import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.adv.dto.ADVAggregatedData;
import org.upyog.dashboard.adv.dto.ADVDTO;
import org.upyog.dashboard.adv.model.ADVMetric;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.transformer.ModuleTransformer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ADVTransformer implements ModuleTransformer<ADVDTO> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Module getModule() {
        return Module.ADV;
    }

    @Override
    public DashboardPayload transform(ADVDTO rawData) {
        ADVAggregatedData combined = rawData.getCombinedMetrics();
        
        if (combined == null) {
            combined = new ADVAggregatedData();
        }

        Integer previousYearRevenue = combined.getPreviousYearRevenue() != null ? combined.getPreviousYearRevenue() : 0;
        Integer currentFYCollection = combined.getCurrentFYCollection() != null ? combined.getCurrentFYCollection() : 0;
        Integer totalApplicationsReceived = combined.getTotalApplicationsReceived() != null ? combined.getTotalApplicationsReceived() : 0;
        Integer totalApplicationsRejected = combined.getTotalApplicationsRejected() != null ? combined.getTotalApplicationsRejected() : 0;
        Integer totalApplicationApproved = combined.getTotalApplicationApproved() != null ? combined.getTotalApplicationApproved() : 0;

        List<Map<String, Object>> transactions = List.of(Map.of("groupBy", "paymentChannelType", "buckets", parseJsonBuckets(combined.getTransactionsJson())));

        ADVMetric advMetric = ADVMetric.builder()
                .previousYearRevenue(previousYearRevenue)
                .currentFYCollection(currentFYCollection)
                .totalApplicationsReceived(totalApplicationsReceived)
                .totalApplicationsRejected(totalApplicationsRejected)
                .totalApplicationApproved(totalApplicationApproved)
                .transactions(transactions)
                .build();

        DashboardData dashboardData = DashboardData.builder()
                .date(rawData.getDate())
                .module(rawData.getModule())
                .ward(rawData.getWard())
                .ulb(rawData.getUlb())
                .region(rawData.getRegion())
                .state(rawData.getState())
                .metrics(advMetric.toMap())
                .build();

        return DashboardPayload.builder()
                .data(List.of(dashboardData))
                .build();
    }

    private List<Map<String, Object>> parseJsonBuckets(String jsonStr) {
        if (StringUtils.isBlank(jsonStr) || "[]".equals(jsonStr)) {
            return List.of();
        }
        try {
            return objectMapper.readValue(jsonStr, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception exception) {
            return List.of();
        }
    }
}
