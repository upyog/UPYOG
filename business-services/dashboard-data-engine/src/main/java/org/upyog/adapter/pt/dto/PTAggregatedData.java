package org.upyog.adapter.pt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a single combined metrics row fetched from DB.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PTAggregatedData {
    private Integer assessments;
    private Integer todaysTotalApplications;
    private Integer todaysClosedApplications;
    private Integer noOfPropertiesPaidToday;
    private Integer todaysApprovedApplications;
    private Integer todaysApprovedApplicationsWithinSLA;
    private Integer avgDaysForApplicationApproval;
    private String propertiesRegisteredJson;
    private String assessedPropertiesJson;
    private String movedApplicationsJson;
}
