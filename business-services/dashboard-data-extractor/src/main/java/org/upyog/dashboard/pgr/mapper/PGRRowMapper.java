package org.upyog.dashboard.pgr.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.upyog.dashboard.pgr.constants.PGRDatabaseConstants;
import org.upyog.dashboard.pgr.model.RawPgrMetric;

/**
 * Explicit RowMapper implementation for mapping SQL ResultSet rows to {@link RawPgrMetric}
 * using {@link PGRDatabaseConstants} without reflection.
 */
public final class PGRRowMapper {

    /**
     * Private constructor to prevent instantiation of utility mapper class.
     */
    private PGRRowMapper() {}

    /**
     * RowMapper instance mapping ResultSet rows to {@link RawPgrMetric} instances.
     */
    public static final RowMapper<RawPgrMetric> COMBINED_ROW_MAPPER = new RowMapper<RawPgrMetric>() {
        @Override
        public RawPgrMetric mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
            return RawPgrMetric.builder()
                    .tenantid(resultSet.getString(PGRDatabaseConstants.TENANT_ID))
                    .slaachievementjson(resultSet.getString(PGRDatabaseConstants.SLA_ACHIEVEMENT_JSON))
                    .completionratejson(resultSet.getString(PGRDatabaseConstants.COMPLETION_RATE_JSON))
                    .uniquecitizens(getNullableInt(resultSet, PGRDatabaseConstants.UNIQUE_CITIZENS))
                    .complaintsbystatusjson(resultSet.getString(PGRDatabaseConstants.COMPLAINTS_BY_STATUS_JSON))
                    .complaintsbychanneljson(resultSet.getString(PGRDatabaseConstants.COMPLAINTS_BY_CHANNEL_JSON))
                    .complaintsbydepartmentjson(resultSet.getString(PGRDatabaseConstants.COMPLAINTS_BY_DEPARTMENT_JSON))
                    .complaintsbycategoryjson(resultSet.getString(PGRDatabaseConstants.COMPLAINTS_BY_CATEGORY_JSON))
                    .todaysreopenedcomplaintsjson(resultSet.getString(PGRDatabaseConstants.TODAYS_REOPENED_COMPLAINTS_JSON))
                    .todaysopencomplaintsjson(resultSet.getString(PGRDatabaseConstants.TODAYS_OPEN_COMPLAINTS_JSON))
                    .todaysassignedcomplaintsjson(resultSet.getString(PGRDatabaseConstants.TODAYS_ASSIGNED_COMPLAINTS_JSON))
                    .averagesolutiontimejson(resultSet.getString(PGRDatabaseConstants.AVERAGE_SOLUTION_TIME_JSON))
                    .todaysrejectedcomplaintsjson(resultSet.getString(PGRDatabaseConstants.TODAYS_REJECTED_COMPLAINTS_JSON))
                    .todaysreassignedcomplaintsjson(resultSet.getString(PGRDatabaseConstants.TODAYS_REASSIGNED_COMPLAINTS_JSON))
                    .todaysreassignrequestedcomplaintsjson(resultSet.getString(PGRDatabaseConstants.TODAYS_REASSIGN_REQUESTED_COMPLAINTS_JSON))
                    .todaysclosedcomplaintsjson(resultSet.getString(PGRDatabaseConstants.TODAYS_CLOSED_COMPLAINTS_JSON))
                    .todaysresolvedcomplaintsjson(resultSet.getString(PGRDatabaseConstants.TODAYS_RESOLVED_COMPLAINTS_JSON))
                    .build();
        }

        /**
         * Reads an integer column value safely, returning {@code null} if the SQL value was NULL.
         *
         * @param resultSet   the active JDBC ResultSet
         * @param columnLabel the column name to extract
         * @return parsed Integer value or null
         * @throws SQLException on database column access error
         */
        private Integer getNullableInt(ResultSet resultSet, String columnLabel) throws SQLException {
            int columnValue = resultSet.getInt(columnLabel);
            return resultSet.wasNull() ? null : columnValue;
        }
    };
}
