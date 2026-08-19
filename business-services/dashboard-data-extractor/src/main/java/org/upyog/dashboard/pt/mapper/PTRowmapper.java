package org.upyog.dashboard.pt.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.upyog.dashboard.pt.dto.PTCollectionDTO;
import org.upyog.dashboard.pt.dto.PTAggregatedData;
import org.upyog.dashboard.pt.constants.PTDatabaseConstants;

/**
 * Utility containing RowMapper instances for mapping database query results
 * to PT DTOs using constants.
 */
public final class PTRowmapper {

    private PTRowmapper() {
        // Prevent instantiation
    }

    /**
     * Maps a single row from the PT combined metrics query to a {@link PTAggregatedData} object.
     * Integer columns are read via {@code getNullableInt} to correctly handle SQL {@code NULL} values;
     * {@code ResultSet.getInt()} silently returns {@code 0} for {@code NULL} without this guard.
     */
    public static final RowMapper<PTAggregatedData> COMBINED_ROW_MAPPER = new RowMapper<PTAggregatedData>() {
        @Override
        public PTAggregatedData mapRow(ResultSet rs, int rowNum) throws SQLException {
            return PTAggregatedData.builder()
                .assessments(getNullableInt(rs, PTDatabaseConstants.ASSESSMENTS))
                .todaysTotalApplications(getNullableInt(rs, PTDatabaseConstants.TODAYS_TOTAL_APPLICATIONS))
                .todaysClosedApplications(getNullableInt(rs, PTDatabaseConstants.TODAYS_CLOSED_APPLICATIONS))
                .noOfPropertiesPaidToday(getNullableInt(rs, PTDatabaseConstants.NO_OF_PROPERTIES_PAID_TODAY))
                .todaysApprovedApplications(getNullableInt(rs, PTDatabaseConstants.TODAYS_APPROVED_APPLICATIONS))
                .todaysApprovedApplicationsWithinSLA(getNullableInt(rs, PTDatabaseConstants.TODAYS_APPROVED_APPLICATIONS_WITHIN_SLA))
                .avgDaysForApplicationApproval(getNullableInt(rs, PTDatabaseConstants.AVG_DAYS_FOR_APPLICATION_APPROVAL))
                .propertiesRegisteredJson(rs.getString(PTDatabaseConstants.PROPERTIES_REGISTERED_JSON))
                .assessedPropertiesJson(rs.getString(PTDatabaseConstants.ASSESSED_PROPERTIES_JSON))
                .movedApplicationsJson(rs.getString(PTDatabaseConstants.MOVED_APPLICATIONS_JSON))
                .build();
        }
    };

    /**
     * Maps a single row from the PT collection metrics query to a {@link PTCollectionDTO} object.
     * Double columns are read via {@code getNullableDouble} to correctly handle SQL {@code NULL} values.
     */
    public static final RowMapper<PTCollectionDTO> COLLECTION_ROW_MAPPER = new RowMapper<PTCollectionDTO>() {
        @Override
        public PTCollectionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return PTCollectionDTO.builder()
                .usageCategory(rs.getString(PTDatabaseConstants.USAGE_CATEGORY))
                .paymentMode(rs.getString(PTDatabaseConstants.PAYMENT_MODE))
                .paymentId(rs.getString(PTDatabaseConstants.PAYMENT_ID))
                .taxHeadCode(rs.getString(PTDatabaseConstants.TAX_HEAD_CODE))
                .taxHeadAmount(getNullableDouble(rs, PTDatabaseConstants.TAX_HEAD_AMOUNT))
                .build();
        }
    };

    /**
     * Reads an integer column from the given {@link ResultSet} and returns {@code null}
     * if the SQL value was {@code NULL}, avoiding the silent {@code 0} default that
     * {@link ResultSet#getInt} produces.
     *
     * @param rs          the result set positioned at the current row
     * @param columnLabel the column label (alias) to read
     * @return the integer value, or {@code null} if the column was SQL {@code NULL}
     * @throws SQLException if a database access error occurs
     */
    private static Integer getNullableInt(ResultSet rs, String columnLabel) throws SQLException {
        int value = rs.getInt(columnLabel);
        return rs.wasNull() ? null : value;
    }

    /**
     * Reads a double column from the given {@link ResultSet} and returns {@code null}
     * if the SQL value was {@code NULL}, avoiding the silent {@code 0.0} default that
     * {@link ResultSet#getDouble} produces.
     *
     * @param rs          the result set positioned at the current row
     * @param columnLabel the column label (alias) to read
     * @return the double value, or {@code null} if the column was SQL {@code NULL}
     * @throws SQLException if a database access error occurs
     */
    private static Double getNullableDouble(ResultSet rs, String columnLabel) throws SQLException {
        double value = rs.getDouble(columnLabel);
        return rs.wasNull() ? null : value;
    }
}
