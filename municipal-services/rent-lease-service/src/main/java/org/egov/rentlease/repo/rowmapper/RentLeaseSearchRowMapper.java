package org.egov.rentlease.repo.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;



import org.egov.rentlease.model.AuditDetails;
import org.egov.rentlease.model.RentLease;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RentLeaseSearchRowMapper implements RowMapper<RentLease>{
	@Autowired
	private ObjectMapper objectMapper;
	
	
	public RentLease mapRow(ResultSet rs, int rowNum) throws SQLException{
		
		 AuditDetails audit = AuditDetails.builder()
	                .createdBy(rs.getString("created_by"))
	                .createdDate(rs.getLong("created_date"))
	                .lastModifiedBy(rs.getString("last_modified_by"))
	                .lastModifiedDate(rs.getLong("last_modified_date"))
	                .build();
		 
		 RentLease rentLease = RentLease.builder()
				 .uuid(rs.getString("uuid"))
				 .tenantId(rs.getString("tenantid"))
				 .mobileNo(rs.getString("mobile_no"))
				 .startDate(rs.getLong("start_date"))
				 .endDate(rs.getLong("end_date"))
				 .months(rs.getLong("month"))
				 .assetId(rs.getString("assetid"))
				 .applicantDetails(getAapplicationDetail(rs,"applicant_detail"))
				 .status(rs.getString("status"))
				 .isActive(rs.getBoolean("is_active"))
				 .auditDetails(audit)
				 .applicationNo(rs.getString("applicationno"))
				 .build();
		
		return rentLease;
	}
	private JsonNode getAapplicationDetail(ResultSet rs, String columnLabel) {
    	JsonNode jsonNode = null;
    	try {
    		String jsonString = rs.getString(columnLabel);
            if (jsonString != null) {
                jsonNode = objectMapper.readTree(jsonString);
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
