package org.egov.asset.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.kafka.Producer;
import org.egov.asset.repository.querybuilder.AssetDisposalQueryBuilder;
import org.egov.asset.repository.rowmapper.AssetDisposalRowMapper;
import org.egov.asset.repository.rowmapper.AssetRowMapper;
import org.egov.asset.web.models.disposal.AssetDisposal;
import org.egov.asset.web.models.disposal.AssetDisposalRequest;
import org.egov.asset.web.models.disposal.AssetDisposalSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
public class AssetDisposeRepository {

    @Autowired
    private Producer producer;

    @Autowired
    private AssetConfiguration config;

    @Autowired
    private AssetDisposalQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    AssetDisposalRowMapper  assetDisposalRowMapper;

    public void save(AssetDisposalRequest assetDisposalRequest) {
        producer.push(config.getSaveAssetDisposal(), assetDisposalRequest);
    }

    public void update(AssetDisposalRequest assetDisposalRequest) {
        producer.push(config.getUpdateAssetDisposal(), assetDisposalRequest);
    }

    public List<AssetDisposal> search(AssetDisposalSearchCriteria searchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = null;
        if (searchCriteria != null) {
            query = queryBuilder.getDisposalSearchQuery(searchCriteria, preparedStmtList);
            log.info("Final asset disposal search query: {}", query);
            return jdbcTemplate.query(query, preparedStmtList.toArray(), assetDisposalRowMapper);
        }
        return  null;
    }
}
