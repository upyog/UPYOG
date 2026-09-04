package org.upyog.dashboard.chb.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.upyog.dashboard.chb.constants.CHBDatabaseConstants;
import org.upyog.dashboard.chb.model.RawChbMetric;

/**
 * Factory class holding explicit RowMapper instances for the CHB module.
 * Maps SQL ResultSet rows to {@link RawChbMetric} without reflection.
 */
public final class CHBRowMapper {

    private CHBRowMapper() {}

    /**
     * Maps a single row from the CHB combined metrics query to a {@link RawChbMetric} object.
     */
    public static final RowMapper<RawChbMetric> COMBINED_ROW_MAPPER = new RowMapper<RawChbMetric>() {
        @Override
        public RawChbMetric mapRow(ResultSet rs, int rowNum) throws SQLException {
            return RawChbMetric.builder()
                    .tenantid(rs.getString(CHBDatabaseConstants.TENANT_ID))
                    .totalActiveVenueAvailable(getNullableInt(rs, CHBDatabaseConstants.TOTAL_ACTIVE_VENUE_AVAILABLE))
                    .totalApplicationReceived(getNullableInt(rs, CHBDatabaseConstants.TOTAL_APPLICATION_RECEIVED))
                    .totalCollections(getNullableInt(rs, CHBDatabaseConstants.TOTAL_COLLECTIONS))
                    .noShowBookings(getNullableInt(rs, CHBDatabaseConstants.NO_SHOW_BOOKINGS))
                    .bookingsJson(rs.getString(CHBDatabaseConstants.BOOKINGS_JSON))
                    .createdByListJson(rs.getString(CHBDatabaseConstants.CREATED_BY_LIST_JSON))
                    .build();
        }

        private Integer getNullableInt(ResultSet rs, String col) throws SQLException {
            int v = rs.getInt(col);
            return rs.wasNull() ? null : v;
        }
    };
}
