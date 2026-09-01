package org.upyog.dashboard.transformer.impl;

import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.chb.dto.CHBAggregatedData;
import org.upyog.dashboard.chb.dto.CHBDTO;
import org.upyog.dashboard.chb.model.CHBMetric;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.transformer.ModuleTransformer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CHBTransformer implements ModuleTransformer<CHBDTO> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Module getModule() {
        return Module.CHB;
    }

    @Override
    public DashboardPayload transform(CHBDTO rawData) {
        CHBAggregatedData combined = rawData.getCombinedMetrics();
        
        if (combined == null) {
            combined = new CHBAggregatedData();
        }

        Integer totalActiveVenueAvailable = combined.getTotalActiveVenueAvailable() != null ? combined.getTotalActiveVenueAvailable() : 0;
        Integer totalApplicationReceived = combined.getTotalApplicationReceived() != null ? combined.getTotalApplicationReceived() : 0;
        Integer totalCollections = combined.getTotalCollections() != null ? combined.getTotalCollections() : 0;
        Integer noShowBookings = combined.getNoShowBookings() != null ? combined.getNoShowBookings() : 0;

        List<Map<String, Object>> bookings = List.of(Map.of("groupBy", "bookingStatus", "buckets", parseJsonBuckets(combined.getBookingsJson())));
        List<Map<String, Object>> bookingType = List.of(Map.of("groupBy", "bookingMode", "buckets", parseJsonBuckets(combined.getBookingTypeJson())));

        CHBMetric chbMetric = CHBMetric.builder()
                .totalActiveVenueAvailable(totalActiveVenueAvailable)
                .totalApplicationReceived(totalApplicationReceived)
                .totalCollections(totalCollections)
                .noShowBookings(noShowBookings)
                .bookings(bookings)
                .bookingType(bookingType)
                .build();

        DashboardData dashboardData = DashboardData.builder()
                .date(rawData.getDate())
                .module(rawData.getModule())
                .ward(rawData.getWard())
                .ulb(rawData.getUlb())
                .region(rawData.getRegion())
                .state(rawData.getState())
                .metrics(chbMetric.toMap())
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
