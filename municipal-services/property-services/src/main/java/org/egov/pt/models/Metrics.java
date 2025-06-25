package org.egov.pt.models;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Metrics {
    private BigInteger assessments = BigInteger.ZERO;
    private BigInteger todaysTotalApplications = BigInteger.ZERO;
    private BigInteger todaysClosedApplications = BigInteger.ZERO;
    private BigInteger noOfPropertiesPaidToday = BigInteger.ZERO;
    private BigInteger todaysApprovedApplications = BigInteger.ZERO;
    private BigInteger todaysApprovedApplicationsWithinSLA= BigInteger.ZERO;
    private BigInteger pendingApplicationsBeyondTimeline = BigInteger.ZERO;
    private BigInteger avgDaysForApplicationApproval = BigInteger.ZERO;
    private BigInteger StipulatedDays= BigInteger.ZERO;

    private List<TodaysMovedApplications> todaysMovedApplications = new ArrayList<>();
    private List<PropertiesRegistered> propertiesRegistered = new ArrayList<>();
    private List<AssessedProperties> assessedProperties = new ArrayList<>();
    private List<Transactions> transactions = new ArrayList<>();
    private List<TodaysCollection> todaysCollection = new ArrayList<>();
    private List<PropertyTax> propertyTax = new ArrayList<>();
    private List<Cess> cess = new ArrayList<>();
    private List<Rebate> rebate = new ArrayList<>();
    private List<Penalty> penalty = new ArrayList<>();
    private List<Interest> interest = new ArrayList<>();
}

