package org.egov.pt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.egov.pt.models.AuditDetails;
import org.egov.pt.models.PtTaxCalculatorTracker;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class PtTaxCalculatorTrackerRowMapper implements RowMapper<PtTaxCalculatorTracker> {

	@Override
	public PtTaxCalculatorTracker mapRow(ResultSet rs, int rowNum) throws SQLException {

		AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("createdby"))
				.lastModifiedBy(rs.getString("lastmodifiedby")).createdTime(rs.getLong("createdtime"))
				.lastModifiedTime(rs.getLong("lastmodifiedtime")).build();

		return PtTaxCalculatorTracker.builder().uuid(rs.getString("uuid")).propertyId(rs.getString("propertyid"))
				.tenantId(rs.getString("tenantid")).financialYear(rs.getString("financialyear"))
				.fromDate(purseToDate(rs.getString("fromdate"))).toDate(purseToDate(rs.getString("todate")))
				.propertyTax(rs.getBigDecimal("propertytax")).auditDetails(auditDetails).build();
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