package org.egov.asset.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetAssignment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class AssetLimitedDateRowMapper implements ResultSetExtractor<List<Asset>> {

	/**
	 * extract the data from the resultset and prepare the BPA Object
	 * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
	 */
    @Override
	public List<Asset> extractData(ResultSet rs) throws SQLException, DataAccessException {
    	
        Map<String, Asset> assetMap = new LinkedHashMap<>();

        while (rs.next()) {
            String id = rs.getString("id");
            String tenantId = rs.getString("tenantId");
            Asset currentAsset = assetMap.get(id);
            if (currentAsset == null) {
                
                currentAsset = Asset.builder()
                	    .id(id)
                	    .tenantId(tenantId)
                	    .applicationNo(rs.getString("applicationNo"))
                	    .assetClassification(rs.getString("classification"))
                	    .assetParentCategory(rs.getString("parentCategory"))
                	    .assetCategory(rs.getString("category"))
                	    .assetSubCategory(rs.getString("subCategory"))
                	    .assetName(rs.getString("name"))
                	    .department(rs.getString("department"))
                	    .status(rs.getString("status"))
                	    .build();

                assetMap.put(id, currentAsset);
            }
            addChildrenToProperty(rs, currentAsset);
        }

        return new ArrayList<>(assetMap.values());
    }

    private void addChildrenToProperty(ResultSet rs, Asset asset) throws SQLException {
	        
	    // Mapping AssignmDetails
	    AssetAssignment assetAssignment = new AssetAssignment();
	    	assetAssignment.setAssignedUserName(rs.getString("assignedUserName"));
	    	assetAssignment.setAssignedDate(rs.getLong("assignedDate"));
	    	assetAssignment.setReturnDate(rs.getLong("assignedDate"));
	    	assetAssignment.setIsAssigned(rs.getBoolean("isAssigned"));
	    	asset.setAssetAssignment(assetAssignment);
    }
}
