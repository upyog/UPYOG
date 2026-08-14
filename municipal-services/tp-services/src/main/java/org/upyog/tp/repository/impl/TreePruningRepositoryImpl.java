package org.upyog.tp.repository.impl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.kafka.Producer;
import org.upyog.tp.repository.TreePruningRepository;
import org.upyog.tp.repository.querybuilder.TreePruningQueryBuilder;
import org.upyog.tp.repository.rowMapper.GenericRowMapper;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;
import org.upyog.tp.web.models.treePruning.TreePruningBookingSearchCriteria;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class TreePruningRepositoryImpl implements TreePruningRepository {

    private final Producer producer;
    private final TreePruningQueryBuilder queryBuilder;
    private final JdbcTemplate jdbcTemplate;
    private final TreePruningConfiguration treePruningConfiguration;

    public TreePruningRepositoryImpl(Producer producer, TreePruningQueryBuilder queryBuilder,
                                       JdbcTemplate jdbcTemplate, TreePruningConfiguration treePruningConfiguration) {
        this.producer = producer;
        this.queryBuilder = queryBuilder;
        this.jdbcTemplate = jdbcTemplate;
        this.treePruningConfiguration = treePruningConfiguration;
    }

    @Override
    public void saveTreePruningBooking(TreePruningBookingRequest treePruningRequest) {
        log.info("Saving tree pruning booking request data for booking no : "
                + treePruningRequest.getTreePruningBookingDetail().getBookingNo());
        pushTreePruningRequestToKafka(treePruningRequest);
    }

    private void pushTreePruningRequestToKafka(TreePruningBookingRequest treePruningRequest) {
        if (Boolean.TRUE.equals(treePruningConfiguration.getIsUserProfileEnabled())) {
            producer.push(treePruningConfiguration.getTreePruningApplicationWithProfileSaveTopic(), treePruningRequest);
        } else {
            producer.push(treePruningConfiguration.getTreePruningApplicationSaveTopic(), treePruningRequest);
        }
    }


    @Override
    public List<TreePruningBookingDetail> getTreePruningBookingDetails(
            TreePruningBookingSearchCriteria treePruningBookingSearchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getTreePruningQuery(treePruningBookingSearchCriteria, preparedStmtList);
        log.info("Final query for getTreePruningBookingDetails {} and paramsList {} : " , preparedStmtList);
        return jdbcTemplate.query(query, new GenericRowMapper<>(TreePruningBookingDetail.class),
                preparedStmtList.toArray());
    }

    @Override
    public Integer getApplicationsCount(TreePruningBookingSearchCriteria criteria) {
        List<Object> preparedStatement = new ArrayList<>();
        String query = queryBuilder.getTreePruningQuery(criteria, preparedStatement);

        if (query == null) {
            return 0;
        }

        log.info("Final query for getTreePruningBookingDetails {} and paramsList {} : " , preparedStatement);

        return jdbcTemplate.queryForObject(query, Integer.class, preparedStatement.toArray());
    }

    @Override
    public void updateTreePruningBooking(TreePruningBookingRequest treePruningRequest) {
        log.info("Updating Tree Pruning request data for booking no : "
                + treePruningRequest.getTreePruningBookingDetail().getBookingNo());
        producer.push(treePruningConfiguration.getTreePruningApplicationUpdateTopic(), treePruningRequest);

    }

}
