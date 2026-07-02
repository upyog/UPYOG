package org.egov.pgr.repository.rowmapper;

import org.egov.pgr.web.models.SwachhImageData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SwachhImageDataRowMapper implements RowMapper<SwachhImageData> {

    @Override
    public SwachhImageData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SwachhImageData.builder()
                .id(rs.getString("id"))
                .tenantId(rs.getString("tenant_id"))
                .userUuid(rs.getString("uuid"))
                .latitude(rs.getDouble("latitude"))
                .longitude(rs.getDouble("longitude"))
                .locality(rs.getString("locality"))
                .imagerUrl(rs.getString("imagerurl"))
                .createdTime(rs.getLong("created_time"))
                .build();
    }
}
