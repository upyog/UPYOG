package org.ksmart.birth.birthregistry.repository.rowmapperforcorrectionapplication;

import org.ksmart.birth.common.model.AuditDetails;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BaseRegCorrRowMapper {
    default AuditDetails getAuditDetails(ResultSet rs) throws SQLException {
        return AuditDetails.builder()
//                .createdBy(rs.getString("createdby"))
//                .createdTime(Long.valueOf(rs.getLong("createdtime")))
                .lastModifiedBy(rs.getString("lastmodifiedby"))
                .lastModifiedTime(Long.valueOf(rs.getLong("lastmodifiedtime")))
                .build();
    }
}
