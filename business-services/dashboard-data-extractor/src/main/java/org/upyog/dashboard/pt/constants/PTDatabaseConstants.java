package org.upyog.dashboard.pt.constants;

/**
 * Constants representing the database column and alias names returned by
 * the Property Tax (PT) module SQL queries in pt-schema-mapping.yml.
 */
public final class PTDatabaseConstants {

    /**
     * Private constructor to prevent instantiation of constant class.
     */
    private PTDatabaseConstants() {
        // Prevent instantiation
    }

    /** Column alias for tenant identifier. */
    public static final String TENANT_ID = "tenantid";

    // Combined metrics query column/alias names (in lowercase as returned by PostgreSQL)
    /** Column alias for total assessments count. */
    public static final String ASSESSMENTS = "assessments";
    /** Column alias for today's total applications received. */
    public static final String TODAYS_TOTAL_APPLICATIONS = "todaystotalapplications";
    /** Column alias for today's closed applications count. */
    public static final String TODAYS_CLOSED_APPLICATIONS = "todaysclosedapplications";
    /** Column alias for number of properties paid today. */
    public static final String NO_OF_PROPERTIES_PAID_TODAY = "noofpropertiespaidtoday";
    /** Column alias for today's approved applications count. */
    public static final String TODAYS_APPROVED_APPLICATIONS = "todaysapprovedapplications";
    /** Column alias for today's approved applications within SLA count. */
    public static final String TODAYS_APPROVED_APPLICATIONS_WITHIN_SLA = "todaysapprovedapplicationswithinsla";
    /** Column alias for average days for application approval. */
    public static final String AVG_DAYS_FOR_APPLICATION_APPROVAL = "avgdaysforapplicationapproval";
    /** Column alias for JSON array of registered property details. */
    public static final String PROPERTIES_REGISTERED_JSON = "propertiesregisteredjson";
    /** Column alias for JSON array of assessed property details. */
    public static final String ASSESSED_PROPERTIES_JSON = "assessedpropertiesjson";
    /** Column alias for JSON array of moved/forwarded applications. */
    public static final String MOVED_APPLICATIONS_JSON = "movedapplicationsjson";

    // Collection metrics query column/alias names
    /** Column alias for usage category (e.g., RESIDENTIAL, COMMERCIAL). */
    public static final String USAGE_CATEGORY = "usage_category";
    /** Column alias for payment mode (e.g., CASH, ONLINE). */
    public static final String PAYMENT_MODE = "paymentmode";
    /** Column alias for unique payment identifier. */
    public static final String PAYMENT_ID = "payment_id";
    /** Column alias for tax head code. */
    public static final String TAX_HEAD_CODE = "taxheadcode";
    /** Column alias for tax head collection amount. */
    public static final String TAX_HEAD_AMOUNT = "tax_head_amount";
}
