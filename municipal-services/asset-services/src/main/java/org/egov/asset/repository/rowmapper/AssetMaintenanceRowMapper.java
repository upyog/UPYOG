package org.egov.asset.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AuditDetails;
import org.egov.asset.web.models.Document;
import org.egov.asset.web.models.maintenance.AssetMaintenance;
import org.postgresql.util.PGobject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@SuppressWarnings("java:S2638")
public class AssetMaintenanceRowMapper implements ResultSetExtractor<List<AssetMaintenance>> {

    private final ObjectMapper objectMapper;

    public AssetMaintenanceRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * extract the data from the resultset and prepare the BPA Object
     *
     * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
     */
    @Override
    public List<AssetMaintenance> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<String, AssetMaintenance> maintenanceMap = new LinkedHashMap<>();

        while (rs.next()) {
            String maintenanceId = rs.getString("maintenance_id");
            AssetMaintenance assetMaintenance = maintenanceMap.computeIfAbsent(maintenanceId, id -> {
                try {
                    return AssetMaintenance.builder()
                            .maintenanceId(id)
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
                            .assetMaintenanceDate(rs.getLong("asset_maintenance_date"))
                            .assetNextMaintenanceDate(rs.getLong("asset_next_maintenance_date"))
                            .additionalDetails(mapAdditionalDetails(rs))
                            .documents(new ArrayList<>())
                            .build();
                } catch (SQLException e) {
                    throw new IllegalStateException(e);
                }
            });

            addChildrenToProperty(rs, assetMaintenance);
        }

        return new ArrayList<>(maintenanceMap.values());
    }

    /**
     * Adds AuditDetails and Documents to AssetMaintenance.
     *
     * @param rs              ResultSet containing data
     * @param assetMaintenance AssetMaintenance object to populate
     */
    private void addChildrenToProperty(ResultSet rs, AssetMaintenance assetMaintenance) throws SQLException {
        Document document = new Document();

        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setCreatedBy(rs.getString("created_by"));
        auditDetails.setCreatedTime(rs.getLong("created_time"));
        auditDetails.setLastModifiedBy(rs.getString("last_modified_by"));
        auditDetails.setLastModifiedTime(rs.getLong("last_modified_time"));
        assetMaintenance.setAuditDetails(auditDetails);

        PGobject additionalDetails = (PGobject) rs.getObject("additional_Details");
        if (additionalDetails != null) {
            try {
                JsonNode additionalDetailsNode = objectMapper.readTree(additionalDetails.getValue());
                assetMaintenance.setAdditionalDetails(additionalDetailsNode);
            } catch (Exception e) {
                // Handle exception
            }
        }

        try {
            String documentId = rs.getString("documentId");
            String documentType = rs.getString("documentType");
            String fileStoreId = rs.getString("fileStoreId");
            String documentUid = rs.getString("documentUid");
            String docDetailsStr = rs.getString("docDetails");

            Object docDetails = null;
            if (docDetailsStr != null && !docDetailsStr.isEmpty()) {
                docDetails = new Gson().fromJson(docDetailsStr, Object.class);
            }

            document.setDocumentId(documentId);
            document.setDocumentType(documentType);
            document.setFileStoreId(fileStoreId);
            document.setDocumentUid(documentUid);
            document.setDocDetails(docDetails);

        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Document> assetDocuments = assetMaintenance.getDocuments();
        if (assetDocuments == null) {
            assetDocuments = new ArrayList<>();
            assetMaintenance.setDocuments(assetDocuments);
        }
        assetDocuments.add(document);
    }

    /**
     * Maps additionalDetails from the ResultSet.
     *
     * @param rs ResultSet containing data
     * @return JsonNode representing additional details, or {@code null} when absent or parsing fails
     */
    private @Nullable JsonNode mapAdditionalDetails(ResultSet rs) {
        try {
            PGobject additionalDetails = (PGobject) rs.getObject("additional_details");
            if (additionalDetails != null) {
                return objectMapper.readTree(additionalDetails.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
