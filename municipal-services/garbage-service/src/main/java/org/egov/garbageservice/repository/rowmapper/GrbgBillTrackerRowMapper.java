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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/** Row mapper that converts a SQL ResultSet row into a GrbgBillTracker domain object. */
@Component
public class GrbgBillTrackerRowMapper implements RowMapper<GrbgBillTracker> {

	@Override
	public GrbgBillTracker mapRow(ResultSet rs, int rowNum) throws SQLException {

		AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("created_by"))
				.lastModifiedBy(rs.getString("last_modified_by")).createdDate(rs.getLong("created_time"))
				.lastModifiedDate(rs.getLong("last_modified_time")).build();
		
		 JsonNode additionalDetail = null;
		    String additionalDetailStr = rs.getString("additionaldetail");
		    if (additionalDetailStr != null) {
		        try {
		            additionalDetail = new ObjectMapper().readTree(additionalDetailStr);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }

		return GrbgBillTracker.builder().uuid(rs.getString("uuid"))
				.grbgApplicationId(rs.getString("grbg_application_id")).tenantId(rs.getString("tenant_id"))
				.month(rs.getString("month")).year(rs.getString("year")).fromDate(rs.getString("from_date"))
				.toDate(rs.getString("to_date")).grbgBillAmount(rs.getBigDecimal("grbg_bill_amount"))
				.billId(rs.getString("bill_id"))
				.demandId(rs.getString("demand_id"))
				.type(rs.getString("type"))
				.status(rs.getString("status"))
				.auditDetails(auditDetails)
				.rebateAmount(rs.getBigDecimal("rebate_amount"))
				.garbageBillWithoutRebate(rs.getBigDecimal("garbage_bill_without_rebate"))
				.additionaldetail(additionalDetail).build();
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