package org.upyog.dashboard.pt.constants;

/**
 * Constants representing the camelCase metric keys for the Property Tax (PT) module
 * that are sent to the National Dashboard and validated by PTValidator.
 */
public final class PTMetricConstants {

    private PTMetricConstants() {
        // Prevent instantiation
    }

    public static final String ASSESSMENTS = "assessments";
    public static final String TODAYS_TOTAL_APPLICATIONS = "todaysTotalApplications";
    public static final String TODAYS_CLOSED_APPLICATIONS = "todaysClosedApplications";
    public static final String NO_OF_PROPERTIES_PAID_TODAY = "noOfPropertiesPaidToday";
    public static final String TODAYS_APPROVED_APPLICATIONS = "todaysApprovedApplications";
    public static final String TODAYS_APPROVED_APPLICATIONS_WITHIN_SLA = "todaysApprovedApplicationsWithinSLA";
    public static final String AVG_DAYS_FOR_APPLICATION_APPROVAL = "avgDaysForApplicationApproval";
    public static final String PROPERTIES_REGISTERED = "propertiesRegistered";
    public static final String ASSESSED_PROPERTIES = "assessedProperties";
    public static final String TRANSACTIONS = "transactions";
    public static final String TODAYS_COLLECTION = "todaysCollection";
    public static final String PROPERTY_TAX = "propertyTax";
    public static final String CESS = "cess";
    public static final String REBATE = "rebate";
    public static final String PENALTY = "penalty";
    public static final String INTEREST = "interest";
}
