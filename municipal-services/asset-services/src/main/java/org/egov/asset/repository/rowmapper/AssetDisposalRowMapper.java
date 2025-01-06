package org.egov.asset.repository.rowmapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.asset.web.models.disposal.AssetDisposal;
import org.egov.asset.web.models.AuditDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AssetDisposalRowMapper implements RowMapper<AssetDisposal> {

    @Autowired
    private final ObjectMapper objectMapper;

    public AssetDisposalRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public AssetDisposal mapRow(ResultSet rs, int rowNum) throws SQLException {
        AssetDisposal disposal = new AssetDisposal();

        disposal.setDisposalId(rs.getString("disposal_id"));
        disposal.setAssetId(rs.getString("asset_id"));
        disposal.setTenantId(rs.getString("tenant_id"));
        disposal.setLifeOfAsset(rs.getLong("life_of_asset"));
        disposal.setCurrentAgeOfAsset(rs.getLong("current_age_of_asset"));
        disposal.setIsAssetDisposedInFacility(rs.getBoolean("is_asset_disposed_in_facility"));
        disposal.setDisposalDate(rs.getLong("disposal_date"));
        disposal.setReasonForDisposal(rs.getString("reason_for_disposal"));
        disposal.setAmountReceived(rs.getDouble("amount_received"));
        disposal.setPurchaserName(rs.getString("purchaser_name"));
        disposal.setPaymentMode(rs.getString("payment_mode"));
        disposal.setReceiptNumber(rs.getString("receipt_number"));
        disposal.setComments(rs.getString("comments"));
        disposal.setGlCode(rs.getString("gl_code"));

        // Mapping AuditDetails
        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setCreatedBy(rs.getString("created_by"));
        auditDetails.setCreatedTime(rs.getLong("created_at"));
        auditDetails.setLastModifiedBy(rs.getString("updated_by"));
        auditDetails.setLastModifiedTime(rs.getLong("updated_at"));
        disposal.setAuditDetails(auditDetails);

        return disposal;
    }
}
