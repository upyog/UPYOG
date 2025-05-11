package org.egov.hrms.repository;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.hrms.model.EmployeeWithWard;
import org.springframework.jdbc.core.RowMapper;

public class EmployeeWardRowMapper implements RowMapper<EmployeeWithWard> {

    public EmployeeWithWard mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Mapping each column from ResultSet to EmployeeWard object
    	EmployeeWithWard employeeWard = new EmployeeWithWard();

        employeeWard.setUuid  (rs.getString("employee_uuid"));
        employeeWard.setEmployeeId(rs.getString("employee_id"));
        employeeWard.setTenantId(rs.getString("employee_tenantid"));
        employeeWard.setWardId(rs.getString("employee_wardid"));

        return employeeWard;
    }
}