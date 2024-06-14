package org.egov.wscalculation.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.wscalculation.web.models.WaterDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class DemandSchedulerRowMapper implements ResultSetExtractor<List<WaterDetails>> {

	
	
	@Override
	public List<WaterDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<WaterDetails> waterDetailList = new ArrayList<>();
		while (rs.next()) {
			WaterDetails waterDetails=new WaterDetails();
			
			waterDetails.setConnectionExecutionDate(rs.getLong("connectionExecutionDate"));
			waterDetails.setConnectionNo(rs.getString("connectionno"));
			waterDetailList.add(waterDetails);
		}
		return waterDetailList;
	}
}