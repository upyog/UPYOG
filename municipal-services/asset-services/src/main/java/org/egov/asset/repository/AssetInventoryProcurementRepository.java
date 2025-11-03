package org.egov.asset.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.repository.querybuilder.AssetInventoryProcurementQueryBuilder;
import org.egov.asset.repository.rowmapper.AssetInventoryProcurementRowMapper;
import org.egov.asset.web.models.AssetInventoryProcurementRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class AssetInventoryProcurementRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AssetInventoryProcurementQueryBuilder queryBuilder;

    @Autowired
    private AssetInventoryProcurementRowMapper rowMapper;

    public void save(AssetInventoryProcurementRequest procurementRequest) {
        String query = queryBuilder.getInsertQuery();
        Object[] params = queryBuilder.getInsertParams(procurementRequest);
        
        log.info("Executing query: {} with params: {}", query, params);
        jdbcTemplate.update(query, params);
    }

    public void update(AssetInventoryProcurementRequest procurementRequest) {
        String query = queryBuilder.getUpdateQuery();
        Object[] params = queryBuilder.getUpdateParams(procurementRequest);
        
        log.info("Executing query: {} with params: {}", query, params);
        jdbcTemplate.update(query, params);
    }

    public List<AssetInventoryProcurementRequest> search(AssetInventoryProcurementRequest searchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getSearchQuery(searchCriteria, preparedStmtList);
        
        log.info("Executing query: {} with params: {}", query, preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }
}