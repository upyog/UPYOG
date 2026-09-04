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

    private PTRowmapper() {
        // Prevent instantiation
    }

    /**
     * Maps a single row from the PT combined metrics query to a {@link RawPtMetric} object.
     */
    public static final RowMapper<RawPtMetric> COMBINED_ROW_MAPPER = new RowMapper<RawPtMetric>() {
        @Override
        public RawPtMetric mapRow(ResultSet rs, int rowNum) throws SQLException {
            return RawPtMetric.builder()
                .tenantid(rs.getString(PTDatabaseConstants.TENANT_ID))
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
     * Maps a single row from the PT collection metrics query to a {@link RawPtCollection} object.
     */
    public static final RowMapper<RawPtCollection> COLLECTION_ROW_MAPPER = new RowMapper<RawPtCollection>() {
        @Override
        public RawPtCollection mapRow(ResultSet rs, int rowNum) throws SQLException {
            return RawPtCollection.builder()
                .tenantid(rs.getString(PTDatabaseConstants.TENANT_ID))
                .usageCategory(rs.getString(PTDatabaseConstants.USAGE_CATEGORY))
                .paymentMode(rs.getString(PTDatabaseConstants.PAYMENT_MODE))
                .paymentId(rs.getString(PTDatabaseConstants.PAYMENT_ID))
                .taxHeadCode(rs.getString(PTDatabaseConstants.TAX_HEAD_CODE))
                .taxHeadAmount(getNullableDouble(rs, PTDatabaseConstants.TAX_HEAD_AMOUNT))
                .build();
        }
    };

    private static Integer getNullableInt(ResultSet rs, String columnLabel) throws SQLException {
        int value = rs.getInt(columnLabel);
        return rs.wasNull() ? null : value;
    }

    private static Double getNullableDouble(ResultSet rs, String columnLabel) throws SQLException {
        double value = rs.getDouble(columnLabel);
        return rs.wasNull() ? null : value;
    }
}
