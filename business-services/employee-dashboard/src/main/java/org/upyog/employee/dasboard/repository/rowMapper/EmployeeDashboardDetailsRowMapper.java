package org.upyog.employee.dasboard.repository.rowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.upyog.employee.dasboard.repository.impl.ServiceRequestRepositoryImpl;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardDetails;
import org.upyog.employee.dasboard.web.models.ModuleName;

public class EmployeeDashboardDetailsRowMapper implements RowMapper<EmployeeDashboardDetails> {

	private final ModuleName moduleName;
	public static final Logger log = LoggerFactory.getLogger(ServiceRequestRepositoryImpl.class);


	public EmployeeDashboardDetailsRowMapper(ModuleName moduleName) {
		this.moduleName = moduleName;
	}

	private boolean isThereTotalAmt(ResultSet rs, String column) {
		try {
			rs.findColumn(column);
			return true;
		} catch (SQLException sqlex) {
			log.debug("Total column doesn't exist {}", column);
		}

		return false;
	}

	// RowMapper method to extract data from the resultset
	@Override
	public EmployeeDashboardDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
		EmployeeDashboardDetails details = new EmployeeDashboardDetails();
		details.setApplicationReceived(rs.getInt("Total_Applications_Received"));
		details.setApplicationApproved(rs.getInt("Total_Applications_Approved"));
		details.setApplicationPending(rs.getInt("Total_Applications_Pending"));

		// Check if 'Total_Amount' exists in the result set

		if (isThereTotalAmt(rs, "Total_Amount")) {
	        BigDecimal totalAmount = rs.getBigDecimal("Total_Amount");
	        details.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
	    } else {
	        // Set default value if the column is missing
	        details.setTotalAmount(BigDecimal.ZERO);
	    }

		return details;
	}
}