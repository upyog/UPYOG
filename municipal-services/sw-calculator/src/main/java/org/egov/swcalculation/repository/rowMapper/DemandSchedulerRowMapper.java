package org.egov.swcalculation.repository.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.swcalculation.web.models.SewerageDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class DemandSchedulerRowMapper implements ResultSetExtractor<List<SewerageDetails>> {

	@Override
	public List<SewerageDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<SewerageDetails> sewerageDetails = new ArrayList<>();
		while (rs.next()) {
			SewerageDetails sw= new SewerageDetails();
			sw.setConnectionNo(rs.getString("connectionno"));
			sw.setConnectionExecutionDate(rs.getLong("connectionexecutiondate"));
			sewerageDetails.add(sw);
		}
		return sewerageDetails;
	}
}