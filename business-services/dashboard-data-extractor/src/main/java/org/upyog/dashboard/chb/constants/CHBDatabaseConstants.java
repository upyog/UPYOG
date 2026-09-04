package org.upyog.dashboard.chb.constants;

/**
 * SQL result-set column alias constants for the CHB (Community Hall Booking) combined metrics query.
 *
 * Each constant maps to the alias defined in chb-schema-mapping.yml and is used by
 * org.upyog.dashboard.chb.mapper.CHBRowMapper to read columns from the java.sql.ResultSet.
 * Centralising aliases here prevents typo-driven bugs and makes schema changes a single-point edit.
 */
public final class CHBDatabaseConstants {

    private CHBDatabaseConstants() {}

    public static final String TENANT_ID = "tenantid";
    public static final String TOTAL_ACTIVE_VENUE_AVAILABLE = "totalactivevenueavailable";
    public static final String TOTAL_APPLICATION_RECEIVED = "totalapplicationreceived";
    public static final String TOTAL_COLLECTIONS = "totalcollections";
    public static final String NO_SHOW_BOOKINGS = "noshowbookings";
    public static final String BOOKINGS_JSON = "bookingsjson";
    public static final String CREATED_BY_LIST_JSON = "createdbylistjson";
}
