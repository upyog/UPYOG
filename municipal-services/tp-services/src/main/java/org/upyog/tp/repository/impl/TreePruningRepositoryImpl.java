package org.upyog.tp.repository.impl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.kafka.Producer;
import org.upyog.tp.repository.TreePruningRepository;
import org.upyog.tp.repository.querybuilder.TreePruningQueryBuilder;
import org.upyog.tp.repository.rowMapper.GenericRowMapper;
import org.upyog.tp.web.models.PersisterWrapper;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;
import org.upyog.tp.web.models.treePruning.TreePruningBookingSearchCriteria;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class TreePruningRepositoryImpl implements TreePruningRepository {

    @Autowired
    private Producer producer;

    @Autowired
    private TreePruningQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    TreePruningConfiguration treePruningConfiguration;

    @Override
    public void saveTreePruningBooking(TreePruningBookingRequest treePruningRequest) {
        log.info("Saving tree pruning booking request data for booking no : "
                + treePruningRequest.getTreePruningBookingDetail().getBookingNo());
        TreePruningBookingDetail treePruningBookingDetail = treePruningRequest.getTreePruningBookingDetail();
        PersisterWrapper<TreePruningBookingDetail> persisterWrapper = new PersisterWrapper<TreePruningBookingDetail>(
                treePruningBookingDetail);
        pushTreePruningRequestToKafka(treePruningRequest);
    }

    private void pushTreePruningRequestToKafka(TreePruningBookingRequest treePruningRequest) {
        if(treePruningConfiguration.getIsUserProfileEnabled()) {
            producer.push(treePruningConfiguration.getTreePruningApplicationWithProfileSaveTopic(), treePruningRequest);
        } else {
            producer.push(treePruningConfiguration.getTreePruningApplicationSaveTopic(), treePruningRequest);
        }
    }


    @Override
    public List<TreePruningBookingDetail> getTreePruningBookingDetails(
            TreePruningBookingSearchCriteria treePruningBookingSearchCriteria) {
        //create a list to hold the statement parameter and allow addition of parameter based on search criteria
        List<Object> preparedStmtList = new ArrayList<>();

		/*passed the preparedStmtList and search criteria inside the getTreePruningQuery method
		 developed inside query builder to build and get the data as per search criteria*/
        String query = queryBuilder.getTreePruningQuery(treePruningBookingSearchCriteria, preparedStmtList);
        log.info("Final query for getTreePruningBookingDetails {} and paramsList {} : " , preparedStmtList);
        /*
         *  Execute the query using JdbcTemplate with a generic row mapper
         *  Converts result set directly to a list of TreePruningBookingDetail objects
         *  Uses custom GenericRowMapper for flexible and recursive object mapping
         * */
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new GenericRowMapper<>(TreePruningBookingDetail.class));
    }

    @Override
    public Integer getApplicationsCount(TreePruningBookingSearchCriteria criteria) {
        List<Object> preparedStatement = new ArrayList<>();
        String query = queryBuilder.getTreePruningQuery(criteria, preparedStatement);

        if (query == null)
            return 0;

        log.info("Final query for getTreePruningBookingDetails {} and paramsList {} : " , preparedStatement);

        Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);

        return count;
    }

    @Override
    public void updateTreePruningBooking(TreePruningBookingRequest treePruningRequest) {
        log.info("Updating Tree Pruning request data for booking no : "
                + treePruningRequest.getTreePruningBookingDetail().getBookingNo());
        producer.push(treePruningConfiguration.getTreePruningApplicationUpdateTopic(), treePruningRequest);

    }

}
