package org.upyog.dashboard.chb.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.upyog.dashboard.chb.constants.CHBDatabaseConstants;
import org.upyog.dashboard.chb.dto.CHBAggregatedData;

public final class CHBRowMapper {

    private CHBRowMapper() {}

    public static final RowMapper<CHBAggregatedData> COMBINED_ROW_MAPPER = new RowMapper<CHBAggregatedData>() {
        @Override
        public CHBAggregatedData mapRow(ResultSet rs, int rowNum) throws SQLException {
            CHBAggregatedData data = new CHBAggregatedData();
            data.setTotalActiveVenueAvailable(getNullableInt(rs, CHBDatabaseConstants.TOTAL_ACTIVE_VENUE_AVAILABLE));
            data.setTotalApplicationReceived(getNullableInt(rs, CHBDatabaseConstants.TOTAL_APPLICATION_RECEIVED));
            data.setTotalCollections(getNullableInt(rs, CHBDatabaseConstants.TOTAL_COLLECTIONS));
            data.setNoShowBookings(getNullableInt(rs, CHBDatabaseConstants.NO_SHOW_BOOKINGS));
            data.setBookingsJson(rs.getString(CHBDatabaseConstants.BOOKINGS_JSON));
            data.setCreatedByListJson(rs.getString(CHBDatabaseConstants.CREATED_BY_LIST_JSON));
            return data;
        }

        private Integer getNullableInt(ResultSet rs, String col) throws SQLException {
            int v = rs.getInt(col);
            return rs.wasNull() ? null : v;
        }
    };
}
