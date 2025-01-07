package org.egov.pgr.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.pgr.web.models.CountStatusRequest;
import org.egov.pgr.web.models.CountStatusUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class CountRowMapper implements ResultSetExtractor<List<CountStatusUpdate>>{
	
	 @Autowired
	    private ObjectMapper mapper;


	@Override
	public List<CountStatusUpdate> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<CountStatusUpdate> updateList = new ArrayList<>();
		CountStatusUpdate update =null;
		
		while (rs.next()) {
			 update = CountStatusUpdate.builder().closedAfterRejection(rs.getInt("closedAfterRejection"))
					.closedAfterResolution(rs.getInt("closedAfterResolution"))
					.pendingAtLME(rs.getInt("pendingAtLME"))
					.pendingForAssignment(rs.getInt("pendingForAssignment"))
					.pendingForReAssignment(rs.getInt("pendingForReAssignment"))
					.rejected(rs.getInt("rejected"))
					.resolved(rs.getInt("resolved"))
					.dateRange(rs.getString("period"))
						/*
						 * .additionalDetails(getAdditionalDetails(rs,"additionaldetails"))
						 * .tenantId(rs.getString("tenantid")) .serviceCode(rs.getString("servicecode"))
						 */
					.build();
			 updateList.add(update);
		}
		
		
		return updateList;
	}


	private Object getAdditionalDetails(ResultSet rs, String columnLabel) {

    	JsonNode jsonNode = null;
    	try {
    		String jsonString = rs.getString(columnLabel);
            if (jsonString != null) {
                jsonNode = mapper.readTree(jsonString);
            }
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jsonNode;
	
	}

	

}
