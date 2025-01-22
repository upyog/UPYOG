package org.egov.asset.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.asset.web.models.Document;
import org.egov.asset.web.models.disposal.AssetDisposal;
import org.egov.asset.web.models.AuditDetails;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class AssetDisposalRowMapper implements ResultSetExtractor<List<AssetDisposal>> {

    @Autowired
    private final ObjectMapper objectMapper;

    public AssetDisposalRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<AssetDisposal> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, AssetDisposal> disposalMap = new LinkedHashMap<>();

        while (rs.next()) {
            String disposalId = rs.getString("disposal_id");
            AssetDisposal disposal = disposalMap.get(disposalId);

            if (disposal == null) {
                disposal = AssetDisposal.builder()
                        .disposalId(disposalId)
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
                        .assetDisposalStatus(rs.getString("asset_disposal_status"))
                        .additionalDetails(mapAdditionalDetails(rs))
                        .documents(new ArrayList<>()) // Initialize documents list
                        .build();

                disposalMap.put(disposalId, disposal);
            }

            // Add children (documents, audit details) to the AssetDisposal object
            addChildrenToProperty(rs, disposal);
        }

        return new ArrayList<>(disposalMap.values());
    }

    /**
     * Adds AuditDetails and Documents to AssetDisposal.
     *
     * @param rs       ResultSet containing data
     * @param disposal AssetDisposal object to populate
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
            if (documentId != null) {
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

                // Add the document to the AssetDisposal object
                disposal.getDocuments().add(document);
            }
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
