package org.egov.pt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.pt.models.Appeal;
import org.egov.pt.models.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
@Component
public class AppealRowMapper implements ResultSetExtractor<List<Appeal>>{

	@Autowired
	private ObjectMapper mapper;
	
	@Override
	public List<Appeal> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub
		Map<String, Appeal> appealMap = new LinkedHashMap<>();
		while(rs.next())
		{
			String appealUuId = rs.getString("id");
			Appeal currentAppeal = appealMap.get(appealUuId);
			String tenanId = rs.getString("tenantid");
			
			if(null==currentAppeal)
			{
				currentAppeal=Appeal.builder()
						.id(appealUuId)
						.propertyId(rs.getString("propertyid"))
						.status(Status.fromValue(rs.getString("status")))
						.acknowldgementNumber(rs.getString("acknowldgementnumber"))
						.tenantId(tenanId)
						.build();
				
				appealMap.put(appealUuId, currentAppeal);
						
			}
		}
		return new ArrayList<>(appealMap.values());
	}

}
