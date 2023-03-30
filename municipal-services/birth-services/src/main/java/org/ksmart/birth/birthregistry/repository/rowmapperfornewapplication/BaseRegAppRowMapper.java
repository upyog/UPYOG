package org.ksmart.birth.birthregistry.repository.rowmapperfornewapplication;

import org.ksmart.birth.common.model.AuditDetails;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BaseRegAppRowMapper {
    default AuditDetails getAuditDetails(ResultSet rs) throws SQLException {
        return AuditDetails.builder()
                .createdBy(rs.getString("createdby"))
                .createdTime(Long.valueOf(rs.getLong("createdtime")))
                .lastModifiedBy(rs.getString("lastmodifiedby"))
                .lastModifiedTime(Long.valueOf(rs.getLong("lastmodifiedtime")))
                .build();
    }
}
