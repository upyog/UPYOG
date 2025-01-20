package org.egov.asset.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.asset.web.models.Document;
import org.egov.asset.web.models.disposal.AssetDisposal;
import org.egov.asset.web.models.AuditDetails;
import org.egov.asset.web.models.maintenance.AssetMaintenance;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AssetDisposalRowMapper implements RowMapper<AssetDisposal> {

    @Autowired
    private final ObjectMapper objectMapper;

    public AssetDisposalRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public AssetDisposal mapRow(ResultSet rs, int rowNum) throws SQLException {
        AssetDisposal disposal = AssetDisposal.builder()
                .disposalId(rs.getString("disposal_id"))
                .assetId(rs.getString("asset_id"))
                .tenantId(rs.getString("tenant_id"))
                .lifeOfAsset(rs.getLong("life_of_asset"))
                .currentAgeOfAsset(rs.getLong("current_age_of_asset"))
                .isAssetDisposedInFacility(rs.getBoolean("is_asset_disposed_in_facility"))
                .disposalDate(rs.getLong("disposal_date"))
                .reasonForDisposal(rs.getString("reason_for_disposal"))
                .amountReceived(rs.getDouble("amount_received"))
                .purchaserName(rs.getString("purchaser_name"))
                .paymentMode(rs.getString("payment_mode"))
                .receiptNumber(rs.getString("receipt_number"))
                .comments(rs.getString("comments"))
                .glCode(rs.getString("gl_code"))
                .additionalDetails(mapAdditionalDetails(rs))
                .assetDisposalStatus(rs.getString("asset_disposal_status"))
                .build();

        addChildrenToProperty(rs, disposal);

        return disposal;
    }

    /**
     * Adds AuditDetails and Documents to AssetMaintenance.
     *
     * @param rs              ResultSet containing data
     * @param disposal AssetMaintenance object to populate
     */
    private void addChildrenToProperty(ResultSet rs, AssetDisposal disposal) throws SQLException {
        // Mapping AuditDetails
        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setCreatedBy(rs.getString("created_by"));
        auditDetails.setCreatedTime(rs.getLong("created_at"));
        auditDetails.setLastModifiedBy(rs.getString("updated_by"));
        auditDetails.setLastModifiedTime(rs.getLong("updated_at"));
        disposal.setAuditDetails(auditDetails);

        // Mapping Documents
        try {
            String documentId = rs.getString("documentid");
            String documentType = rs.getString("documenttype");
            String fileStoreId = rs.getString("filestoreid");
            String documentUid = rs.getString("documentuid");
            String docDetailsStr = rs.getString("docdetails");

            Object docDetails = null;
            if (docDetailsStr != null && !docDetailsStr.isEmpty()) {
                docDetails = objectMapper.readTree(docDetailsStr);
            }

            Document document = Document.builder()
                    .documentId(documentId)
                    .documentType(documentType)
                    .fileStoreId(fileStoreId)
                    .documentUid(documentUid)
                    .docDetails(docDetails)
                    .build();

            // Add the document to the AssetMaintenance object
            List<Document> documents = disposal.getDocuments();
            if (documents == null) {
                documents = new ArrayList<>();
                disposal.setDocuments(documents);
            }
            documents.add(document);
        } catch (Exception e) {
            // Handle exceptions during document mapping
            e.printStackTrace();
        }
    }

    /**
     * Maps additionalDetails from the ResultSet.
     *
     * @param rs ResultSet containing data
     * @return JsonNode representing additional details
     */
    private JsonNode mapAdditionalDetails(ResultSet rs) {
        try {
            PGobject additionalDetails = (PGobject) rs.getObject("additional_details");
            if (additionalDetails != null) {
                return objectMapper.readTree(additionalDetails.getValue());
            }
        } catch (Exception e) {
            // Handle exceptions during additional details mapping
            e.printStackTrace();
        }
        return null;
    }
}
