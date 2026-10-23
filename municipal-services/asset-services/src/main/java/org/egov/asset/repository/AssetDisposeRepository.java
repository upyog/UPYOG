package org.egov.asset.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.kafka.Producer;
import org.egov.asset.repository.querybuilder.AssetDisposalQueryBuilder;
import org.egov.asset.repository.rowmapper.AssetDisposalRowMapper;
import org.egov.asset.web.models.disposal.AssetDisposal;
import org.egov.asset.web.models.disposal.AssetDisposalRequest;
import org.egov.asset.web.models.disposal.AssetDisposalSearchCriteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@Repository
public class AssetDisposeRepository {

    private final Producer producer;
    private final AssetConfiguration config;
    private final AssetDisposalQueryBuilder queryBuilder;
    private final JdbcTemplate jdbcTemplate;
    private final AssetDisposalRowMapper assetDisposalRowMapper;

    public AssetDisposeRepository(Producer producer, AssetConfiguration config,
                                  AssetDisposalQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate,
                                  AssetDisposalRowMapper assetDisposalRowMapper) {
        this.producer = producer;
        this.config = config;
        this.queryBuilder = queryBuilder;
        this.jdbcTemplate = jdbcTemplate;
        this.assetDisposalRowMapper = assetDisposalRowMapper;
    }

    public void save(AssetDisposalRequest assetDisposalRequest) {
        producer.push(config.getSaveAssetDisposal(), assetDisposalRequest);
    }

    public void update(AssetDisposalRequest assetDisposalRequest) {
        producer.push(config.getUpdateAssetDisposal(), assetDisposalRequest);
    }

    public List<AssetDisposal> search(AssetDisposalSearchCriteria searchCriteria) {
        if (searchCriteria == null) {
            return Collections.emptyList();
        }
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getDisposalSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final asset disposal search query: {}", query);
        return jdbcTemplate.query(query, assetDisposalRowMapper, preparedStmtList.toArray());
    }
}
