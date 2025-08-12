package org.egov.asset.repository.rowmapper;

import org.egov.asset.web.models.AssetAssignment;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssetAssignmentRowMapper implements RowMapper<AssetAssignment> {
    @Override
    public AssetAssignment mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AssetAssignment.builder()
                .assignmentId(rs.getString("assignmentid"))
                .assetApplicaltionNo(rs.getString("applicationno"))
                .tenantId(rs.getString("tenantid"))
                .assignedUserName(rs.getString("assignedusername"))
                .employeeCode(rs.getString("employeecode"))
                .designation(rs.getString("designation"))
                .department(rs.getString("department"))
                .assignedDate(rs.getLong("assigneddate"))
                .isAssigned(rs.getBoolean("isassigned"))
                .returnDate(rs.getLong("returndate"))
                .build();
    }
}
