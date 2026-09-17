package org.upyog.dashboard.pt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * DTO representing a single combined metrics row fetched from DB.
 */
@Getter
@Setter
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
