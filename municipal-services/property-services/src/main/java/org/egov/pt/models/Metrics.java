package org.egov.pt.models;

import java.math.BigInteger;
import java.util.List;

import lombok.Data;

@Data
public class Metrics {
    private BigInteger assessments;
    private BigInteger todaysTotalApplications;
    private BigInteger todaysClosedApplications;
    private BigInteger noOfPropertiesPaidToday;
    private BigInteger todaysApprovedApplications;
    private BigInteger todaysApprovedApplicationsWithinSLA;
    private BigInteger pendingApplicationsBeyondTimeline;
    private BigInteger avgDaysForApplicationApproval;
    private BigInteger StipulatedDays;
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
