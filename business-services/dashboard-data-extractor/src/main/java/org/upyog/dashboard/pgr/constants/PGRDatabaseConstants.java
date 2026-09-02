package org.upyog.dashboard.pgr.constants;

/**
 * Constants representing the database column and alias names returned by
 * the Public Grievance Redressal (PGR) module SQL queries in pgr-schema-mapping.yml.
 */
public final class PGRDatabaseConstants {

    private PGRDatabaseConstants() {
        // Prevent instantiation
    }

    public static final String TENANT_ID = "tenantid";
    public static final String UNIQUE_CITIZENS = "uniquecitizens";
    public static final String SLA_ACHIEVEMENT_JSON = "slaachievementjson";
    public static final String COMPLETION_RATE_JSON = "completionratejson";
    public static final String COMPLAINTS_BY_STATUS_JSON = "complaintsbystatusjson";
    public static final String COMPLAINTS_BY_CHANNEL_JSON = "complaintsbychanneljson";
    public static final String COMPLAINTS_BY_DEPARTMENT_JSON = "complaintsbydepartmentjson";
    public static final String COMPLAINTS_BY_CATEGORY_JSON = "complaintsbycategoryjson";
    public static final String TODAYS_REOPENED_COMPLAINTS_JSON = "todaysreopenedcomplaintsjson";
    public static final String TODAYS_OPEN_COMPLAINTS_JSON = "todaysopencomplaintsjson";
    public static final String TODAYS_ASSIGNED_COMPLAINTS_JSON = "todaysassignedcomplaintsjson";
    public static final String AVERAGE_SOLUTION_TIME_JSON = "averagesolutiontimejson";
    public static final String TODAYS_REJECTED_COMPLAINTS_JSON = "todaysrejectedcomplaintsjson";
    public static final String TODAYS_REASSIGNED_COMPLAINTS_JSON = "todaysreassignedcomplaintsjson";
    public static final String TODAYS_REASSIGN_REQUESTED_COMPLAINTS_JSON = "todaysreassignrequestedcomplaintsjson";
    public static final String TODAYS_CLOSED_COMPLAINTS_JSON = "todaysclosedcomplaintsjson";
    public static final String TODAYS_RESOLVED_COMPLAINTS_JSON = "todaysresolvedcomplaintsjson";
}
