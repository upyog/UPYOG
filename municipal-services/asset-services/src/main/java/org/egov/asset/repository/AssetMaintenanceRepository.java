package org.egov.asset.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.kafka.Producer;
import org.egov.asset.repository.querybuilder.AssetMaintenanceQueryBuilder;
import org.egov.asset.repository.rowmapper.AssetMaintenanceRowMapper;
import org.egov.asset.web.models.maintenance.AssetMaintenance;
import org.egov.asset.web.models.maintenance.AssetMaintenanceRequest;
import org.egov.asset.web.models.maintenance.AssetMaintenanceSearchCriteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class AssetMaintenanceRepository {

    private final AssetMaintenanceQueryBuilder queryBuilder;
    private final Producer producer;
    private final AssetConfiguration config;
    private final JdbcTemplate jdbcTemplate;
    private final AssetMaintenanceRowMapper rowMapper;

    public AssetMaintenanceRepository(AssetMaintenanceQueryBuilder queryBuilder, Producer producer,
                                      AssetConfiguration config, JdbcTemplate jdbcTemplate,
                                      AssetMaintenanceRowMapper rowMapper) {
        this.queryBuilder = queryBuilder;
        this.producer = producer;
        this.config = config;
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    public void save(AssetMaintenanceRequest request) {
        producer.push(config.getSaveAssetMaintenance(), request);
    }

    public void update(AssetMaintenanceRequest request) {
        producer.push(config.getUpdateAssetMaintenance(), request);
    }

    public List<AssetMaintenance> search(AssetMaintenanceSearchCriteria searchCriteria) {
        if (searchCriteria == null) {
            return Collections.emptyList();
        }
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getMaintenanceSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final asset maintenance search query: {}", query);
        return jdbcTemplate.query(query, rowMapper, preparedStmtList.toArray());
    }
}
