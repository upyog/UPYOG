package org.egov.garbageservice.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.model.AuditDetails;
import org.egov.garbageservice.model.GrbgBillTracker;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class GrbgBillTrackerRowMapper implements RowMapper<GrbgBillTracker> {

	@Override
	public GrbgBillTracker mapRow(ResultSet rs, int rowNum) throws SQLException {

		AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("created_by"))
				.lastModifiedBy(rs.getString("last_modified_by")).createdDate(rs.getLong("created_time"))
				.lastModifiedDate(rs.getLong("last_modified_time")).build();

		return GrbgBillTracker.builder().uuid(rs.getString("uuid"))
				.grbgApplicationId(rs.getString("grbg_application_id")).tenantId(rs.getString("tenant_id"))
				.month(rs.getString("month")).year(rs.getString("year")).fromDate(rs.getString("from_date"))
				.toDate(rs.getString("to_date")).grbgBillAmount(rs.getBigDecimal("grbg_bill_amount"))
				.auditDetails(auditDetails).build();
	}

	private Date purseToDate(String dateString) {
		// Specify the date format (assuming "dd-MM-yyyy")
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		if (!StringUtils.isEmpty(dateString)) {
			try {
				// Parse the date string to a Date object
				Date date = dateFormat.parse(dateString);
				return date;
			} catch (Exception e) {
				e.printStackTrace(); // Handle parsing errors
			}
		}
		return null;
	}

}