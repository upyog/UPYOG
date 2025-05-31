package org.egov.pt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.egov.pt.models.AuditDetails;
import org.egov.pt.models.PtTaxCalculatorTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PtTaxCalculatorTrackerRowMapper implements RowMapper<PtTaxCalculatorTracker> {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public PtTaxCalculatorTracker mapRow(ResultSet rs, int rowNum) throws SQLException {

		AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("createdby"))
				.lastModifiedBy(rs.getString("lastmodifiedby")).createdTime(rs.getLong("createdtime"))
				.lastModifiedTime(rs.getLong("lastmodifiedtime")).build();

		return PtTaxCalculatorTracker.builder().uuid(rs.getString("uuid")).propertyId(rs.getString("propertyid"))
				.tenantId(rs.getString("tenantid")).financialYear(rs.getString("financialyear"))
				.fromDate(purseToDate(rs.getString("fromdate"))).toDate(purseToDate(rs.getString("todate")))
				.propertyTax(rs.getBigDecimal("propertytax")).auditDetails(auditDetails)
				.additionalDetails(getAdditionalDetail(rs, "additionaldetails")).billId(rs.getString("bill_id"))
				.build();
	}

	private JsonNode getAdditionalDetail(ResultSet rs, String column) {
		JsonNode jsonNode = null;
		try {
			String jsonString = rs.getString(column);
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