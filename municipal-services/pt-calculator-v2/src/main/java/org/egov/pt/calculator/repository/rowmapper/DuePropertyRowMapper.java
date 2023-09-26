package org.egov.pt.calculator.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.pt.calculator.web.models.DefaultersInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class DuePropertyRowMapper implements ResultSetExtractor<List<DefaultersInfo>> {

	@Override
	public List<DefaultersInfo> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, DefaultersInfo> defaultersMap = new HashMap<>();
		while (rs.next()) {
			String propertyId = rs.getString("propertyid");
			DefaultersInfo defaulter = defaultersMap.get(propertyId);
			if (null == defaulter) {
				defaulter = DefaultersInfo.builder().propertyId(rs.getString("propertyid"))
						//.ownerName(rs.getString("ownerName")).mobileNumber(rs.getString("mobileNumber"))
						//.dueAmount(rs.getDouble("balance"))
						.tenantId(rs.getString("tenantid"))
						.build();
				;

				defaultersMap.put(propertyId, defaulter);
			}

		}

		return new ArrayList<>(defaultersMap.values());
	}

}
