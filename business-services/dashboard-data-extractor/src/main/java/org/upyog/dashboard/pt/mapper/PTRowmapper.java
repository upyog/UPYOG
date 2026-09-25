package org.upyog.dashboard.pt.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.upyog.dashboard.pt.model.RawPtMetric;
import org.upyog.dashboard.pt.model.RawPtCollection;
import org.upyog.dashboard.pt.constants.PTDatabaseConstants;

/**
 * Utility containing explicit RowMapper instances for mapping database query results
 * to PT models without reflection.
 */
public final class PTRowmapper {

    /**
     * Private constructor to prevent instantiation of utility mapper class.
     */
    private PTRowmapper() {
        // Prevent instantiation
    }

    /**
     * Maps a single row from the PT combined metrics query to a {@link RawPtMetric} object.
     */
    public static final RowMapper<RawPtMetric> COMBINED_ROW_MAPPER = new RowMapper<RawPtMetric>() {
        @Override
        public RawPtMetric mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
            return RawPtMetric.builder()
                .tenantid(resultSet.getString(PTDatabaseConstants.TENANT_ID))
                .assessments(getNullableInt(resultSet, PTDatabaseConstants.ASSESSMENTS))
                .todaysTotalApplications(getNullableInt(resultSet, PTDatabaseConstants.TODAYS_TOTAL_APPLICATIONS))
                .todaysClosedApplications(getNullableInt(resultSet, PTDatabaseConstants.TODAYS_CLOSED_APPLICATIONS))
                .noOfPropertiesPaidToday(getNullableInt(resultSet, PTDatabaseConstants.NO_OF_PROPERTIES_PAID_TODAY))
                .todaysApprovedApplications(getNullableInt(resultSet, PTDatabaseConstants.TODAYS_APPROVED_APPLICATIONS))
                .todaysApprovedApplicationsWithinSLA(getNullableInt(resultSet, PTDatabaseConstants.TODAYS_APPROVED_APPLICATIONS_WITHIN_SLA))
                .avgDaysForApplicationApproval(getNullableInt(resultSet, PTDatabaseConstants.AVG_DAYS_FOR_APPLICATION_APPROVAL))
                .propertiesRegisteredJson(resultSet.getString(PTDatabaseConstants.PROPERTIES_REGISTERED_JSON))
                .assessedPropertiesJson(resultSet.getString(PTDatabaseConstants.ASSESSED_PROPERTIES_JSON))
                .movedApplicationsJson(resultSet.getString(PTDatabaseConstants.MOVED_APPLICATIONS_JSON))
                .build();
        }
    };

    /**
     * Maps a single row from the PT collection metrics query to a {@link RawPtCollection} object.
     */
    public static final RowMapper<RawPtCollection> COLLECTION_ROW_MAPPER = new RowMapper<RawPtCollection>() {
        @Override
        public RawPtCollection mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
            return RawPtCollection.builder()
                .tenantid(resultSet.getString(PTDatabaseConstants.TENANT_ID))
                .usageCategory(resultSet.getString(PTDatabaseConstants.USAGE_CATEGORY))
                .paymentMode(resultSet.getString(PTDatabaseConstants.PAYMENT_MODE))
                .paymentId(resultSet.getString(PTDatabaseConstants.PAYMENT_ID))
                .taxHeadCode(resultSet.getString(PTDatabaseConstants.TAX_HEAD_CODE))
                .taxHeadAmount(getNullableDouble(resultSet, PTDatabaseConstants.TAX_HEAD_AMOUNT))
                .build();
        }
    };

    /**
     * Reads an integer column value safely, returning {@code null} if the SQL value was NULL.
     *
     * @param resultSet   the active JDBC ResultSet
     * @param columnLabel the column name to extract
     * @return parsed Integer value or null
     * @throws SQLException on database column access error
     */
    private static Integer getNullableInt(ResultSet resultSet, String columnLabel) throws SQLException {
        int value = resultSet.getInt(columnLabel);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Reads a double column value safely, returning {@code null} if the SQL value was NULL.
     *
     * @param resultSet   the active JDBC ResultSet
     * @param columnLabel the column name to extract
     * @return parsed Double value or null
     * @throws SQLException on database column access error
     */
    private static Double getNullableDouble(ResultSet resultSet, String columnLabel) throws SQLException {
        double value = resultSet.getDouble(columnLabel);
        return resultSet.wasNull() ? null : value;
    }
}
