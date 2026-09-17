package org.upyog.dashboard.adv.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Transformed metrics model for Advertisement (ADV) formatted for downstream dashboard ingestion.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ADVMetric {
    private Integer previousYearRevenue;
    private Integer currentFYCollection;
    private Integer totalApplicationsReceived;
    private Integer totalApplicationsRejected;
    private Integer totalApplicationApproved;
    private List<Map<String, Object>> transactions;

    /**
     * Serializes this metric object into a flat key-value Map structure for payload formatting.
     *
     * @return map of metric field names to values
     */
    public Map<String, Object> toMap() {
        Map<String, Object> dataMap = new java.util.LinkedHashMap<>();
        dataMap.put("previousYearRevenue", previousYearRevenue != null ? previousYearRevenue : 0);
        dataMap.put("currentFYCollection", currentFYCollection != null ? currentFYCollection : 0);
        dataMap.put("totalApplicationsReceived", totalApplicationsReceived != null ? totalApplicationsReceived : 0);
        dataMap.put("totalApplicationsRejected", totalApplicationsRejected != null ? totalApplicationsRejected : 0);
        dataMap.put("totalApplicationApproved", totalApplicationApproved != null ? totalApplicationApproved : 0);
        dataMap.put("transactions", transactions != null ? transactions : List.of());
        return dataMap;
    }
}
