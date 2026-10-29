package upyog.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import upyog.web.models.Asset;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class AssetRowMapper implements ResultSetExtractor<List<Asset>> {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Extracts database query result rows directly into an ArrayList of assets.
     */
    @Override
    public List<Asset> extractData(ResultSet rs) throws SQLException {
        List<Asset> assets = new ArrayList<>();
        
        while (rs.next()) {
            Asset asset = new Asset();
            
            // Set basic fields
            asset.setAssetId(rs.getString("asset_id"));
            asset.setEstateNo(rs.getString("estate_no"));
            asset.setTenantId(rs.getString("tenant_id"));
            asset.setBuildingName(rs.getString("building_name"));
            asset.setBuildingNo(rs.getString("building_no"));
            asset.setRefAssetNo(rs.getString("ref_asset_no"));
            
            // Set numeric fields
            asset.setFloor(rs.getInt("floor"));
            asset.setTotalFloorArea(getBigDecimal(rs, "total_floor_area"));
            asset.setDimensionLength(getBigDecimal(rs, "dimension_length"));
            asset.setDimensionWidth(getBigDecimal(rs, "dimension_width"));
            asset.setRate(getBigDecimal(rs, "rent_rate"));
            
            // Set locality fields
            asset.setLocalityCode(rs.getString("locality_code"));
            asset.setLocality(rs.getString("locality"));
            
            // Set asset details
            asset.setAssetName(rs.getString("name"));
            asset.setDescription(rs.getString("description"));
            asset.setAssetClassification(rs.getString("classification"));
            asset.setAssetParentCategory(rs.getString("parent_category"));
            asset.setAssetCategory(rs.getString("category"));
            asset.setAssetSubCategory(rs.getString("subcategory"));
            asset.setDepartment(rs.getString("department"));
            asset.setAssetType(rs.getString("asset_type"));
            asset.setAssetStatus(rs.getString("asset_status"));
            asset.setAssetAllotmentType(rs.getString("asset_allotment_type"));
            asset.setAssetAllotmentStatus(rs.getString("asset_allotment_status"));
            
            // Set additional details (JSONB)
            asset.setAdditionalDetails(getAdditionalDetails(rs, "additional_details"));
            
            AuditDetails auditDetails = AuditDetails.builder()
                    .createdBy(rs.getString("createdby"))
                    .createdTime(rs.getLong("createdtime"))
                    .lastModifiedBy(rs.getString("lastmodifiedby"))
                    .lastModifiedTime(rs.getLong("lastmodifiedtime"))
                    .build();
            asset.setAuditDetails(auditDetails);
            
            assets.add(asset);
        }
        
        return assets;
    }
    
    /**
     * Helper method to safely get BigDecimal from ResultSet
     */
    private BigDecimal getBigDecimal(ResultSet rs, String columnName) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnName);
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Helper method to parse additional details from JSONB
     */
    private Object getAdditionalDetails(ResultSet rs, String columnName) throws SQLException {
        try {
            PGobject pgObject = (PGobject) rs.getObject(columnName);
            if (pgObject != null) {
                String json = pgObject.getValue();
                if (json != null && !json.isEmpty()) {
                    return objectMapper.readValue(json, JsonNode.class);
                }
            }
        } catch (IOException e) {
            log.error("Error parsing additional details: ", e);
        }
        return null;
    }
}
