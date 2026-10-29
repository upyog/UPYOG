package org.upyog.dashboard.pgr.constants;

/**
 * Constants representing the database column and alias names returned by
 * the Public Grievance Redressal (PGR) module SQL queries in pgr-schema-mapping.yml.
 */
public final class PGRDatabaseConstants {

    /**
     * Private constructor to prevent instantiation of constant class.
     */
    private PGRDatabaseConstants() {
        // Prevent instantiation
    }

    /** Column alias for tenant identifier. */
    public static final String TENANT_ID = "tenantid";
    /** Column alias for count of unique citizens. */
    public static final String UNIQUE_CITIZENS = "uniquecitizens";
    /** Column alias for JSON array of SLA achievement metrics. */
    public static final String SLA_ACHIEVEMENT_JSON = "slaachievementjson";
    /** Column alias for JSON array of completion rate metrics. */
    public static final String COMPLETION_RATE_JSON = "completionratejson";
    /** Column alias for JSON array of complaints grouped by status. */
    public static final String COMPLAINTS_BY_STATUS_JSON = "complaintsbystatusjson";
    /** Column alias for JSON array of complaints grouped by channel. */
    public static final String COMPLAINTS_BY_CHANNEL_JSON = "complaintsbychanneljson";
    /** Column alias for JSON array of complaints grouped by department. */
    public static final String COMPLAINTS_BY_DEPARTMENT_JSON = "complaintsbydepartmentjson";
    /** Column alias for JSON array of complaints grouped by category. */
    public static final String COMPLAINTS_BY_CATEGORY_JSON = "complaintsbycategoryjson";
    /** Column alias for JSON array of today's reopened complaints. */
    public static final String TODAYS_REOPENED_COMPLAINTS_JSON = "todaysreopenedcomplaintsjson";
    /** Column alias for JSON array of today's open complaints. */
    public static final String TODAYS_OPEN_COMPLAINTS_JSON = "todaysopencomplaintsjson";
    /** Column alias for JSON array of today's assigned complaints. */
    public static final String TODAYS_ASSIGNED_COMPLAINTS_JSON = "todaysassignedcomplaintsjson";
    /** Column alias for JSON array of average solution time metrics. */
    public static final String AVERAGE_SOLUTION_TIME_JSON = "averagesolutiontimejson";
    /** Column alias for JSON array of today's rejected complaints. */
    public static final String TODAYS_REJECTED_COMPLAINTS_JSON = "todaysrejectedcomplaintsjson";
    /** Column alias for JSON array of today's reassigned complaints. */
    public static final String TODAYS_REASSIGNED_COMPLAINTS_JSON = "todaysreassignedcomplaintsjson";
    /** Column alias for JSON array of today's reassign-requested complaints. */
    public static final String TODAYS_REASSIGN_REQUESTED_COMPLAINTS_JSON = "todaysreassignrequestedcomplaintsjson";
    /** Column alias for JSON array of today's closed complaints. */
    public static final String TODAYS_CLOSED_COMPLAINTS_JSON = "todaysclosedcomplaintsjson";
    /** Column alias for JSON array of today's resolved complaints. */
    public static final String TODAYS_RESOLVED_COMPLAINTS_JSON = "todaysresolvedcomplaintsjson";
}
