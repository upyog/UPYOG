package org.egov.echallan.repository.rowmapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@SuppressWarnings("java:S2638")
public class ChallanCountRowMapper implements ResultSetExtractor<Map<String,String>> {

	private static final String COUNT_COLUMN = "count";

	/**
	 * Maps ResultSet rows to aggregated challan counts by application status.
	 *
	 * @param rs the JDBC result set containing grouped count rows
	 * @return map of active, paid, cancelled, and total challan counts as strings
	 * @throws SQLException if a database access error occurs
	 * @throws DataAccessException if a data-access error occurs during extraction
	 */
	@Override
	public Map<String,String> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String,String> response = new HashMap<>();
		int totalChallan = 0;
		int activeChallan = 0;
		int paidChallan = 0;
		int cancelledChallan = 0;

		while(rs.next()) {
			if(rs.getString("applicationstatus").equalsIgnoreCase("CANCELLED"))
				cancelledChallan = cancelledChallan + rs.getInt(COUNT_COLUMN);
			else if(rs.getString("applicationstatus").equalsIgnoreCase("ACTIVE"))
				activeChallan = activeChallan + rs.getInt(COUNT_COLUMN);
			else
				paidChallan = paidChallan + rs.getInt(COUNT_COLUMN);

			totalChallan = totalChallan + rs.getInt(COUNT_COLUMN);
		}
		if(totalChallan==0){
			response.put("activeChallan","0");
			response.put("paidChallan", "0");
			response.put("cancelledChallan", "0");
			response.put("totalChallan", "0");
			return response;
		}
		response.put("activeChallan", String.valueOf(activeChallan));
		response.put("paidChallan", String.valueOf(paidChallan));
		response.put("cancelledChallan", String.valueOf(cancelledChallan));
		response.put("totalChallan", String.valueOf(totalChallan));

		return response;
	}


}
