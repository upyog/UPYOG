package upyog.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import upyog.config.EstateConfiguration;
import upyog.kafka.Producer;
import upyog.repository.EstateRepository;
import upyog.repository.builder.AllotmentQueryBuilder;
import upyog.repository.builder.AssetQueryBuilder;
import upyog.repository.rowmapper.AllotmentRowmapper;
import upyog.repository.rowmapper.AssetRowMapper;
import upyog.web.models.Allotment;
import upyog.web.models.AllotmentSearchCriteria;
import upyog.web.models.Asset;
import upyog.web.models.AssetSearchCriteria;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class EstateRepositoryImpl implements EstateRepository {
    private final EstateConfiguration estateConfiguration;
    private final Producer producer;
    private final JdbcTemplate jdbcTemplate;
    private final AssetQueryBuilder assetQueryBuilder;
    private final AssetRowMapper assetRowMapper;
    private final AllotmentQueryBuilder allotmentQueryBuilder;
    private final AllotmentRowmapper allotmentRowmapper;

    @Override
    public void save(String topic, Object value) {
        log.info("Saving data to Kafka topic: {}", topic);
        log.info("Value to be saved: {}", value);
        producer.push(topic, value);
    }

    @Override
    public List<Asset> searchAssets(AssetSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = assetQueryBuilder.getAssetSearchQuery(criteria, preparedStmtList);
        
        log.info("Executing asset search query: {}", query);
        log.info("Query parameters: {}", preparedStmtList);
        
        List<Asset> assets = jdbcTemplate.query(query, preparedStmtList.toArray(), assetRowMapper);
        
        log.info("Found {} assets matching the criteria", assets != null ? assets.size() : 0);
        return assets != null ? assets : new ArrayList<>();
    }

    @Override
    public List<Allotment> searchAllotments(AllotmentSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = allotmentQueryBuilder.getAllotmentSearchQuery(criteria, preparedStmtList);
        
        log.info("Executing allotment search query: {}", query);
        log.info("Query parameters: {}", preparedStmtList);
        
        List<Allotment> allotments = jdbcTemplate.query(query, preparedStmtList.toArray(), allotmentRowmapper);
        
        log.info("Found {} allotments matching the criteria", allotments != null ? allotments.size() : 0);
        return allotments != null ? allotments : new ArrayList<>();
    }
}
