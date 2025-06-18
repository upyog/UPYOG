package org.egov.pt.models;

import java.util.List;

import lombok.Data;

@Data
public class Metrics {
    private int assessments;
    private int todaysTotalApplications;
    private int todaysClosedApplications;
    private int noOfPropertiesPaidToday;
    private int todaysApprovedApplications;
    private int todaysApprovedApplicationsWithinSLA;
    private int pendingApplicationsBeyondTimeline;
    private int avgDaysForApplicationApproval;
    private int StipulatedDays;
    private List<TodaysMovedApplications> todaysMovedApplications;
    private List<PropertiesRegistered> propertiesRegistered;
    private List<AssessedProperties> assessedProperties;
    private List<Transactions> transactions;
    private List<TodaysCollection> todaysCollection;
    private List<PropertyTax> propertyTax;
    private List<Cess> cess;
    private List<Rebate> rebate;
    private List<Penalty> penalty;
    private List<Interest> interest;
}
