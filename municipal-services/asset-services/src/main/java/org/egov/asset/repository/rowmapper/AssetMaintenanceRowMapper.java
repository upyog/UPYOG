package org.egov.asset.repository.rowmapper;

import org.egov.asset.web.models.maintenance.AssetMaintenance;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AssetMaintenanceRowMapper implements RowMapper<AssetMaintenance> {

    @Override
    public AssetMaintenance mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AssetMaintenance.builder()
                .maintenanceId(rs.getString("maintenance_id"))
                .assetId(rs.getString("asset_id"))
                .currentLifeOfAsset(rs.getString("current_life_of_asset"))
                .isWarrantyExpired(rs.getBoolean("is_warranty_expired"))
                .isAMCExpired(rs.getBoolean("is_amc_expired"))
                .warrantyStatus(rs.getString("warranty_status"))
                .amcDetails(rs.getString("amc_details"))
                .maintenanceType(rs.getString("maintenance_type"))
                .paymentType(rs.getString("payment_type"))
                .costOfMaintenance(rs.getDouble("cost_of_maintenance"))
                .vendor(rs.getString("vendor"))
                .maintenanceCycle(rs.getString("maintenance_cycle"))
                .partsAddedOrReplaced(rs.getString("parts_added_or_replaced"))
                .postConditionRemarks(rs.getString("post_condition_remarks"))
                .preConditionRemarks(rs.getString("pre_condition_remarks"))
                .description(rs.getString("description"))
                .build();
    }
}
