package com.mseva.gisintegration.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public class TenantMappingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getTownNameByTenantId(String tenantId) {
        String sql = "SELECT municipal_town FROM municipal_tenant_mapping WHERE tenantid = ? LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, tenantId);
        } catch (Exception e) {
            // Return null or a default value if the mapping isn't found
            return null; 
        }
    }
    
    public Map<String, Object> getBoundaryDetails(String localityCode, String tenantId) {
        String sql = "SELECT zonename, zonecode, blockname, blockcode, localityname " +
                     "FROM eg_bndry_mohalla WHERE localitycode = ? AND tenantid = ? LIMIT 1";
        try {
            // Using queryForMap because it returns multiple columns as key-value pairs
            return jdbcTemplate.queryForMap(sql, localityCode, tenantId);
        } catch (Exception e) {
            // Log the error if necessary, but return null to prevent sync failure
            return null;
        }
    }
}