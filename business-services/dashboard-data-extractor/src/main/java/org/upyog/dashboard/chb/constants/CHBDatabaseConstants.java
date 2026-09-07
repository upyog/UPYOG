package org.upyog.dashboard.chb.constants;

/**
 * SQL result-set column alias constants for the CHB (Community Hall Booking) combined metrics query.
 *
 * Each constant maps to the alias defined in chb-schema-mapping.yml and is used by
 * org.upyog.dashboard.chb.mapper.CHBRowMapper to read columns from the java.sql.ResultSet.
 * Centralising aliases here prevents typo-driven bugs and makes schema changes a single-point edit.
 */
public final class CHBDatabaseConstants {

    /**
     * Private constructor to prevent instantiation of constant class.
     */
    private CHBDatabaseConstants() {}

    /** Column alias for tenant identifier. */
    public static final String TENANT_ID = "tenantid";
    /** Column alias for total active venues available. */
    public static final String TOTAL_ACTIVE_VENUE_AVAILABLE = "totalactivevenueavailable";
    /** Column alias for total applications received. */
    public static final String TOTAL_APPLICATION_RECEIVED = "totalapplicationreceived";
    /** Column alias for total collections amount. */
    public static final String TOTAL_COLLECTIONS = "totalcollections";
    /** Column alias for total no-show bookings count. */
    public static final String NO_SHOW_BOOKINGS = "noshowbookings";
    /** Column alias for JSON array of booking records. */
    public static final String BOOKINGS_JSON = "bookingsjson";
    /** Column alias for JSON array of creator user details. */
    public static final String CREATED_BY_LIST_JSON = "createdbylistjson";
}
