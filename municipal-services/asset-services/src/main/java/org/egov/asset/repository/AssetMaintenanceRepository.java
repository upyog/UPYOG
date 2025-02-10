package org.egov.asset.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.kafka.Producer;
import org.egov.asset.repository.querybuilder.AssetMaintenanceQueryBuilder;
import org.egov.asset.repository.rowmapper.AssetMaintenanceRowMapper;
import org.egov.asset.web.models.maintenance.AssetMaintenance;
import org.egov.asset.web.models.maintenance.AssetMaintenanceRequest;
import org.egov.asset.web.models.maintenance.AssetMaintenanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class AssetMaintenanceRepository {

    @Autowired
    private AssetMaintenanceQueryBuilder queryBuilder;

    @Autowired
    private Producer producer;

    @Autowired
    private AssetConfiguration config;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AssetMaintenanceRowMapper rowMapper;

    public void save(AssetMaintenanceRequest request) {
        producer.push(config.getSaveAssetMaintenance(), request);
    }

    public void update(AssetMaintenanceRequest request) {
        producer.push(config.getUpdateAssetMaintenance(), request);
    }

    public List<AssetMaintenance> search(AssetMaintenanceSearchCriteria searchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = null;
        if (searchCriteria != null) {
            query = queryBuilder.getMaintenanceSearchQuery(searchCriteria, preparedStmtList);
            log.info("Final asset maintenance search query: {}", query);
            return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
        }
        return  null;
    }
}
