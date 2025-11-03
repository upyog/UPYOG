package org.egov.asset.repository.rowmapper;

import org.egov.asset.web.models.AssetInventoryProcurementRequest;
import org.egov.asset.web.models.AuditDetails;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AssetInventoryProcurementRowMapper implements RowMapper<AssetInventoryProcurementRequest> {

    @Override
    public AssetInventoryProcurementRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuditDetails auditDetails = AuditDetails.builder()
                .createdBy(rs.getString("created_by"))
                .createdTime(rs.getLong("created_time"))
                .lastModifiedBy(rs.getString("last_modified_by"))
                .lastModifiedTime(rs.getLong("last_modified_time"))
                .build();

        return AssetInventoryProcurementRequest.builder()
                .requestId(rs.getString("request_id"))
                .item(rs.getString("item"))
                .itemType(rs.getString("item_type"))
                .quantity(rs.getInt("quantity"))
                .assetApplicationNumber(rs.getString("asset_application_number"))
                .tenantId(rs.getString("tenant_id"))
                .status(rs.getString("status"))
                .auditDetails(auditDetails)
                .build();
    }
}