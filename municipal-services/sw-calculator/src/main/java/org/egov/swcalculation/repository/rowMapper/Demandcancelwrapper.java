package org.egov.swcalculation.repository.rowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.egov.swcalculation.web.models.Canceldemandsearch;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
@Component
public class Demandcancelwrapper implements ResultSetExtractor<List<Canceldemandsearch>> {
	@Override
	public List<Canceldemandsearch> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<Canceldemandsearch> waterDetailList = new ArrayList<>();
		while (rs.next()) {
			Canceldemandsearch waterDetails=new Canceldemandsearch();
			
			waterDetails.setDemandid(rs.getString("id"));
			waterDetails.setConsumercode(rs.getString("consumercode"));
			waterDetailList.add(waterDetails);
		}
		return waterDetailList;
	}
}