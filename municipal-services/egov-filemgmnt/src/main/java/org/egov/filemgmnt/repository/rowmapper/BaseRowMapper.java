package org.egov.filemgmnt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.filemgmnt.web.models.AuditDetails;

interface BaseRowMapper {

    default AuditDetails getAuditDetails(ResultSet rs) throws SQLException {
        return AuditDetails.builder()
                           .createdBy(rs.getString("createdby"))
                           .createdTime(Long.valueOf(rs.getLong("createdtime")))
                           .lastModifiedBy(rs.getString("lastmodifiedby"))
                           .lastModifiedTime(Long.valueOf(rs.getLong("lastmodifiedtime")))
                           .build();
    }

}
