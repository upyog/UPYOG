package org.upyog.dashboard.pt.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.upyog.dashboard.pt.constants.PTMetricConstants;

/**
 * Type-safe class representing Property Tax (PT) metrics (acts as PTTran / PTMetric).
 */
@Getter
@Setter
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
     * Converts the type-safe fields into the generic dashboard metrics dataMap.
     *
     * @return the metrics dataMap
     */
    public Map<String, Object> toMap() {
        Map<String, Object> dataMap = new java.util.LinkedHashMap<>();
        dataMap.put(PTMetricConstants.ASSESSMENTS, assessments != null ? assessments : 0);
        dataMap.put(PTMetricConstants.TODAYS_TOTAL_APPLICATIONS, todaysTotalApplications != null ? todaysTotalApplications : 0);
        dataMap.put(PTMetricConstants.TODAYS_CLOSED_APPLICATIONS, todaysClosedApplications != null ? todaysClosedApplications : 0);
        dataMap.put(PTMetricConstants.NO_OF_PROPERTIES_PAID_TODAY, noOfPropertiesPaidToday != null ? noOfPropertiesPaidToday : 0);
        dataMap.put(PTMetricConstants.TODAYS_APPROVED_APPLICATIONS, todaysApprovedApplications != null ? todaysApprovedApplications : 0);
        dataMap.put(PTMetricConstants.TODAYS_APPROVED_APPLICATIONS_WITHIN_SLA, todaysApprovedApplicationsWithinSLA != null ? todaysApprovedApplicationsWithinSLA : 0);
        dataMap.put(PTMetricConstants.AVG_DAYS_FOR_APPLICATION_APPROVAL, avgDaysForApplicationApproval != null ? avgDaysForApplicationApproval : 0);
        dataMap.put(PTMetricConstants.PROPERTIES_REGISTERED, propertiesRegistered != null ? propertiesRegistered : List.of());
        dataMap.put(PTMetricConstants.ASSESSED_PROPERTIES, assessedProperties != null ? assessedProperties : List.of());
        dataMap.put(PTMetricConstants.TRANSACTIONS, transactions != null ? transactions : List.of());
        dataMap.put(PTMetricConstants.TODAYS_COLLECTION, todaysCollection != null ? todaysCollection : List.of());
        dataMap.put(PTMetricConstants.PROPERTY_TAX, propertyTax != null ? propertyTax : List.of());
        dataMap.put(PTMetricConstants.CESS, cess != null ? cess : List.of());
        dataMap.put(PTMetricConstants.REBATE, rebate != null ? rebate : List.of());
        dataMap.put(PTMetricConstants.PENALTY, penalty != null ? penalty : List.of());
        dataMap.put(PTMetricConstants.INTEREST, interest != null ? interest : List.of());
        return dataMap;
    }
}
