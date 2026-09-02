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

    private PGRRowMapper() {}

    public static final RowMapper<RawPgrMetric> COMBINED_ROW_MAPPER = new RowMapper<RawPgrMetric>() {
        @Override
        public RawPgrMetric mapRow(ResultSet rs, int rowNum) throws SQLException {
            return RawPgrMetric.builder()
                    .tenantid(rs.getString(PGRDatabaseConstants.TENANT_ID))
                    .slaachievementjson(rs.getString(PGRDatabaseConstants.SLA_ACHIEVEMENT_JSON))
                    .completionratejson(rs.getString(PGRDatabaseConstants.COMPLETION_RATE_JSON))
                    .uniquecitizens(getNullableInt(rs, PGRDatabaseConstants.UNIQUE_CITIZENS))
                    .complaintsbystatusjson(rs.getString(PGRDatabaseConstants.COMPLAINTS_BY_STATUS_JSON))
                    .complaintsbychanneljson(rs.getString(PGRDatabaseConstants.COMPLAINTS_BY_CHANNEL_JSON))
                    .complaintsbydepartmentjson(rs.getString(PGRDatabaseConstants.COMPLAINTS_BY_DEPARTMENT_JSON))
                    .complaintsbycategoryjson(rs.getString(PGRDatabaseConstants.COMPLAINTS_BY_CATEGORY_JSON))
                    .todaysreopenedcomplaintsjson(rs.getString(PGRDatabaseConstants.TODAYS_REOPENED_COMPLAINTS_JSON))
                    .todaysopencomplaintsjson(rs.getString(PGRDatabaseConstants.TODAYS_OPEN_COMPLAINTS_JSON))
                    .todaysassignedcomplaintsjson(rs.getString(PGRDatabaseConstants.TODAYS_ASSIGNED_COMPLAINTS_JSON))
                    .averagesolutiontimejson(rs.getString(PGRDatabaseConstants.AVERAGE_SOLUTION_TIME_JSON))
                    .todaysrejectedcomplaintsjson(rs.getString(PGRDatabaseConstants.TODAYS_REJECTED_COMPLAINTS_JSON))
                    .todaysreassignedcomplaintsjson(rs.getString(PGRDatabaseConstants.TODAYS_REASSIGNED_COMPLAINTS_JSON))
                    .todaysreassignrequestedcomplaintsjson(rs.getString(PGRDatabaseConstants.TODAYS_REASSIGN_REQUESTED_COMPLAINTS_JSON))
                    .todaysclosedcomplaintsjson(rs.getString(PGRDatabaseConstants.TODAYS_CLOSED_COMPLAINTS_JSON))
                    .todaysresolvedcomplaintsjson(rs.getString(PGRDatabaseConstants.TODAYS_RESOLVED_COMPLAINTS_JSON))
                    .build();
        }

        private Integer getNullableInt(ResultSet rs, String col) throws SQLException {
            int val = rs.getInt(col);
            return rs.wasNull() ? null : val;
        }
    };
}
