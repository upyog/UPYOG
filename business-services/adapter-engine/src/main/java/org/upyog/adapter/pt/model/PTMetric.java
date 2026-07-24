package org.upyog.adapter.pt.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.upyog.adapter.pt.constants.PTMetricConstants;

/**
 * Type-safe class representing Property Tax (PT) metrics (acts as PTTran / PTMetric).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PTMetric {
    private Integer assessments;
    private Integer todaysTotalApplications;
    private Integer todaysClosedApplications;
    private Integer noOfPropertiesPaidToday;
    private Integer todaysApprovedApplications;
    private Integer todaysApprovedApplicationsWithinSLA;
    private Integer avgDaysForApplicationApproval;
    private List<Map<String, Object>> propertiesRegistered;
    private List<Map<String, Object>> assessedProperties;
    private List<Map<String, Object>> transactions;
    private List<Map<String, Object>> todaysCollection;
    private List<Map<String, Object>> propertyTax;
    private List<Map<String, Object>> cess;
    private List<Map<String, Object>> rebate;
    private List<Map<String, Object>> penalty;
    private List<Map<String, Object>> interest;

    /**
     * Converts the type-safe fields into the generic dashboard metrics map.
     *
     * @return the metrics map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put(PTMetricConstants.ASSESSMENTS, assessments != null ? assessments : 0);
        map.put(PTMetricConstants.TODAYS_TOTAL_APPLICATIONS, todaysTotalApplications != null ? todaysTotalApplications : 0);
        map.put(PTMetricConstants.TODAYS_CLOSED_APPLICATIONS, todaysClosedApplications != null ? todaysClosedApplications : 0);
        map.put(PTMetricConstants.NO_OF_PROPERTIES_PAID_TODAY, noOfPropertiesPaidToday != null ? noOfPropertiesPaidToday : 0);
        map.put(PTMetricConstants.TODAYS_APPROVED_APPLICATIONS, todaysApprovedApplications != null ? todaysApprovedApplications : 0);
        map.put(PTMetricConstants.TODAYS_APPROVED_APPLICATIONS_WITHIN_SLA, todaysApprovedApplicationsWithinSLA != null ? todaysApprovedApplicationsWithinSLA : 0);
        map.put(PTMetricConstants.AVG_DAYS_FOR_APPLICATION_APPROVAL, avgDaysForApplicationApproval != null ? avgDaysForApplicationApproval : 0);
        map.put(PTMetricConstants.PROPERTIES_REGISTERED, propertiesRegistered != null ? propertiesRegistered : List.of());
        map.put(PTMetricConstants.ASSESSED_PROPERTIES, assessedProperties != null ? assessedProperties : List.of());
        map.put(PTMetricConstants.TRANSACTIONS, transactions != null ? transactions : List.of());
        map.put(PTMetricConstants.TODAYS_COLLECTION, todaysCollection != null ? todaysCollection : List.of());
        map.put(PTMetricConstants.PROPERTY_TAX, propertyTax != null ? propertyTax : List.of());
        map.put(PTMetricConstants.CESS, cess != null ? cess : List.of());
        map.put(PTMetricConstants.REBATE, rebate != null ? rebate : List.of());
        map.put(PTMetricConstants.PENALTY, penalty != null ? penalty : List.of());
        map.put(PTMetricConstants.INTEREST, interest != null ? interest : List.of());
        return map;
    }
}
