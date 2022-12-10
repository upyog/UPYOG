package org.ksmart.death.crdeath.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ksmart.death.crdeath.web.models.AuditDetails;
/**
     * Creates CrDeathService
     * Rakhi S IKM
     * on  05/12/2022
     */
interface BaseRowMapper {

    default AuditDetails getAuditDetails(ResultSet rs) throws SQLException {
        return AuditDetails.builder()
                           .createdBy(rs.getString("created_by"))
                           .createdTime(Long.valueOf(rs.getLong("createdtime")))
                           .lastModifiedBy(rs.getString("lastmodifiedby"))
                           .lastModifiedTime(Long.valueOf(rs.getLong("lastmodifiedtime")))
                           .build();
    }

}
