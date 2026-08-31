package org.upyog.dashboard.pt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw data class for PT combined metrics fetched directly via
 * BeanPropertyRowMapper.
 * <p>
 * Holds the aggregated assessment, application, and registration counts
 * extracted from the Property Tax data sources, ready for transformation into
 * the final payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawPtMetric {

    private String tenantid;
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
