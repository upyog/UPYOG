package org.egov.asset.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.asset.web.models.AssetAuditDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class AssetAuditRowMapper implements ResultSetExtractor<List<AssetAuditDetails>> {

	@Override
	public List<AssetAuditDetails> extractData(ResultSet rs) throws SQLException, DataAccessException{
		Map<String, AssetAuditDetails> assetMap = new LinkedHashMap<>();
		while(rs.next()) {
			 String tenantId = rs.getString("tenantid");
			 AssetAuditDetails assetAuditDetails = assetMap.get(tenantId);
			 if(assetAuditDetails == null) {
				 assetAuditDetails = AssetAuditDetails.builder().tenantId(tenantId)
						 .category(rs.getString("category")).classification(rs.getString("classification")).parentCategory(rs.getString("parentcategory"))
						 .subCategory(rs.getString("subcategory")).build();
			 }
			 assetMap.put(tenantId, assetAuditDetails);
		}
		
		return new ArrayList<>(assetMap.values());
	}

}
