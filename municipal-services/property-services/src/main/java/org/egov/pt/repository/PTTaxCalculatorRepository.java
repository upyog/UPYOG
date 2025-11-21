package org.egov.pt.repository;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.egov.pt.repository.builder.PropertyQueryBuilder;

import java.util.HashMap;
import java.util.Map;

@Repository
public class PTTaxCalculatorRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void updateTrackerStatus(String tenantId, String consumerCode, String status, String userId) {

        Map<String, Object> params = new HashMap<>();
        params.put("tenantId", tenantId);
        params.put("consumerCode", consumerCode); 
        params.put("status", status);
        params.put("lastModifiedBy", userId);
        params.put("lastModifiedTime", System.currentTimeMillis());

        jdbcTemplate.update(PropertyQueryBuilder.UPDATE_TRACKER_STATUS, params);
    }

}
