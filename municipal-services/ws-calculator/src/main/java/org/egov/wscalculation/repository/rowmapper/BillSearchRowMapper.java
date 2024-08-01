package org.egov.wscalculation.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.egov.wscalculation.web.models.BillSearch;
import org.egov.wscalculation.web.models.Canceldemandsearch;
import org.egov.wscalculation.web.models.WaterDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class BillSearchRowMapper implements ResultSetExtractor<List<BillSearch>> {

	@Override
	public List<BillSearch> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<BillSearch> waterDetailList = new ArrayList<>();
		while (rs.next()) {
			BillSearch waterDetails=new BillSearch();
			
			waterDetails.setId(rs.getString("id"));
			waterDetailList.add(waterDetails);
		}
		return waterDetailList;
	}
}
















