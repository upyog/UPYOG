package org.egov.pt.calculator.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.pt.calculator.web.models.property.AuditDetails;
import org.egov.pt.calculator.web.models.property.Property;
import org.egov.pt.calculator.web.models.property.PropertyInfo.StatusEnum;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class PropertyRowMapper implements ResultSetExtractor<List<Property>> {

	@Override
	public List<Property> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, Property> propertyMap = new HashMap<>();
		while (rs.next()) {
			String propertyId = rs.getString("propertyid");
			Property property = propertyMap.get(propertyId);
			if (null == property) {
				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("createdby"))
						.createdTime(rs.getLong("createdTime")).lastModifiedBy(rs.getString("lastmodifiedby"))
						.lastModifiedTime(rs.getLong("lastmodifiedtime")).build();

				property = Property.builder().acknowldgementNumber(rs.getString("acknowldgementNumber"))
						.status(StatusEnum.fromValue(rs.getString("status")))
						.ownershipCategory(rs.getString("ownershipcategory"))
						.oldPropertyId(rs.getString("oldPropertyId")).propertyType(rs.getString("propertytype"))
						.propertyId(rs.getString("propertyid")).auditDetails(auditDetails)
						.tenantId(rs.getString("tenantid")).build();
				;

				propertyMap.put(propertyId, property);
			}

		}

		return new ArrayList<>(propertyMap.values());
	}

}
