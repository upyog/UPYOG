package org.egov.rentlease.repo.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.rentlease.model.AuditDetails;
import org.egov.rentlease.model.RentLease;
import org.flywaydb.core.internal.jdbc.RowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RentLeaseRowMapper implements RowMapper<RentLease> {
	
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public RentLease mapRow(ResultSet rs) throws SQLException {
		 AuditDetails audit = AuditDetails.builder()
	                .createdBy(rs.getString("created_by"))
	                .createdDate(rs.getLong("created_date"))
	                .lastModifiedBy(rs.getString("last_modified_by"))
	                .lastModifiedDate(rs.getLong("last_modified_date"))
	                .build();
		
		return null;
	}


	
	

}
