package org.egov.asset.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.asset.web.models.AuditDetails;
import org.egov.asset.web.models.Document;
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
public class AssetMaintenanceRowMapper implements RowMapper<AssetMaintenance> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public AssetMaintenance mapRow(ResultSet rs, int rowNum) throws SQLException {
        AssetMaintenance assetMaintenance = AssetMaintenance.builder()
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
                .assetMaintenanceStatus(rs.getString("asset_maintenance_status"))
                .additionalDetails(mapAdditionalDetails(rs))
                .build();

        addChildrenToProperty(rs, assetMaintenance);

        return assetMaintenance;
    }

    /**
     * Adds AuditDetails and Documents to AssetMaintenance.
     *
     * @param rs              ResultSet containing data
     * @param assetMaintenance AssetMaintenance object to populate
     */
    private void addChildrenToProperty(ResultSet rs, AssetMaintenance assetMaintenance) throws SQLException {
        // Mapping AuditDetails
        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setCreatedBy(rs.getString("created_by"));
        auditDetails.setCreatedTime(rs.getLong("created_time"));
        auditDetails.setLastModifiedBy(rs.getString("last_modified_by"));
        auditDetails.setLastModifiedTime(rs.getLong("last_modified_time"));
        assetMaintenance.setAuditDetails(auditDetails);

//        // Mapping Documents
//        try {
//            String documentId = rs.getString("documentid");
//            String documentType = rs.getString("documenttype");
//            String fileStoreId = rs.getString("filestoreid");
//            String documentUid = rs.getString("documentuid");
//            String docDetailsStr = rs.getString("docdetails");
//
//            Object docDetails = null;
//            if (docDetailsStr != null && !docDetailsStr.isEmpty()) {
//                docDetails = objectMapper.readTree(docDetailsStr);
//            }
//
//            Document document = Document.builder()
//                    .documentId(documentId)
//                    .documentType(documentType)
//                    .fileStoreId(fileStoreId)
//                    .documentUid(documentUid)
//                    .docDetails(docDetails)
//                    .build();
//
//            // Add the document to the AssetMaintenance object
//            List<Document> documents = assetMaintenance.getDocuments();
//            if (documents == null) {
//                documents = new ArrayList<>();
//                assetMaintenance.setDocuments(documents);
//            }
//            documents.add(document);
//        } catch (Exception e) {
//            // Handle exceptions during document mapping
//            e.printStackTrace();
//        }
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
