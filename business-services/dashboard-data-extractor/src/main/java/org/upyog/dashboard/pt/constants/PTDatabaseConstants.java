package org.upyog.dashboard.pt.constants;

/**
 * Constants representing the database column and alias names returned by
 * the Property Tax (PT) module SQL queries in pt-schema-mapping.yml.
 */
public final class PTDatabaseConstants {

    private PTDatabaseConstants() {
        // Prevent instantiation
    }

    public static final String TENANT_ID = "tenantid";

    // Combined metrics query column/alias names (in lowercase as returned by PostgreSQL)
    public static final String ASSESSMENTS = "assessments";
    public static final String TODAYS_TOTAL_APPLICATIONS = "todaystotalapplications";
    public static final String TODAYS_CLOSED_APPLICATIONS = "todaysclosedapplications";
    public static final String NO_OF_PROPERTIES_PAID_TODAY = "noofpropertiespaidtoday";
    public static final String TODAYS_APPROVED_APPLICATIONS = "todaysapprovedapplications";
    public static final String TODAYS_APPROVED_APPLICATIONS_WITHIN_SLA = "todaysapprovedapplicationswithinsla";
    public static final String AVG_DAYS_FOR_APPLICATION_APPROVAL = "avgdaysforapplicationapproval";
    public static final String PROPERTIES_REGISTERED_JSON = "propertiesregisteredjson";
    public static final String ASSESSED_PROPERTIES_JSON = "assessedpropertiesjson";
    public static final String MOVED_APPLICATIONS_JSON = "movedapplicationsjson";

    // Collection metrics query column/alias names
    public static final String USAGE_CATEGORY = "usage_category";
    public static final String PAYMENT_MODE = "paymentmode";
    public static final String PAYMENT_ID = "payment_id";
    public static final String TAX_HEAD_CODE = "taxheadcode";
    public static final String TAX_HEAD_AMOUNT = "tax_head_amount";
}
