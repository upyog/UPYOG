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

    /**
     * Private constructor to prevent instantiation of utility mapper class.
     */
    private CHBRowMapper() {}

    /**
     * Maps a single row from the CHB combined metrics query to a {@link RawChbMetric} object.
     */
    public static final RowMapper<RawChbMetric> COMBINED_ROW_MAPPER = new RowMapper<RawChbMetric>() {
        @Override
        public RawChbMetric mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
            return RawChbMetric.builder()
                    .tenantid(resultSet.getString(CHBDatabaseConstants.TENANT_ID))
                    .totalActiveVenueAvailable(getNullableInt(resultSet, CHBDatabaseConstants.TOTAL_ACTIVE_VENUE_AVAILABLE))
                    .totalApplicationReceived(getNullableInt(resultSet, CHBDatabaseConstants.TOTAL_APPLICATION_RECEIVED))
                    .totalCollections(getNullableInt(resultSet, CHBDatabaseConstants.TOTAL_COLLECTIONS))
                    .noShowBookings(getNullableInt(resultSet, CHBDatabaseConstants.NO_SHOW_BOOKINGS))
                    .bookingsJson(resultSet.getString(CHBDatabaseConstants.BOOKINGS_JSON))
                    .createdByListJson(resultSet.getString(CHBDatabaseConstants.CREATED_BY_LIST_JSON))
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
