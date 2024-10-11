package org.upyog.employee.dasboard.repository.rowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardDetails;
import org.upyog.employee.dasboard.web.models.ModuleName;

public class EmployeeDashboardDetailsRowMapper implements RowMapper<EmployeeDashboardDetails> {
    
    private final ModuleName moduleName;

    public EmployeeDashboardDetailsRowMapper(ModuleName moduleName) {
        this.moduleName = moduleName;
    }
 
    
    //RowMapper method to extract data from the resultset
    @Override
    public EmployeeDashboardDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        EmployeeDashboardDetails details = new EmployeeDashboardDetails();
        details.setApplicationReceived(rs.getInt("Total_Applications_Received"));
        details.setApplicationApproved(rs.getInt("Total_Applications_Approved"));
        details.setApplicationPending(rs.getInt("Total_Applications_Pending"));

        // Check if 'Total_Amount' exists in the result set
        ResultSetMetaData rsMetaData = rs.getMetaData();
        boolean hasTotalAmount = false;
        for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
            if ("Total_Amount".equalsIgnoreCase(rsMetaData.getColumnName(i))) {
                hasTotalAmount = true;
                break;
            }
        }

        if (hasTotalAmount) {
            BigDecimal totalAmount = rs.getBigDecimal("Total_Amount");
            details.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
        } else {
            // Default value if the column is missing
            details.setTotalAmount(BigDecimal.ZERO);
        }

        return details;
    }
}