package org.egov.asset.calculator.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CustomDepreciationRepositoryImpl implements CustomDepreciationRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomDepreciationRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void executeBulkDepreciationProcedure(String tenantId) {
        String sql = "CALL calculate_depreciation(?, ?, ?)";
        jdbcTemplate.update(sql, tenantId, null, false); // assetId will be replaced with null
    }

    @Override
    public void executeSingleAndLegacyDataBulkDepreciationCalProcedure(String tenantId, String assetId) {
        String sql = "CALL calculate_depreciation(?, ?, ?)";
        jdbcTemplate.update(sql, tenantId, assetId, true);
    }
}
