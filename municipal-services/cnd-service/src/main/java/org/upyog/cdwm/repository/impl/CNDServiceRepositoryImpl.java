package org.upyog.cdwm.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.kafka.Producer;
import org.upyog.cdwm.repository.CNDServiceRepository;
import org.upyog.cdwm.repository.querybuilder.CNDServiceQueryBuilder;
import org.upyog.cdwm.repository.rowMapper.GenericRowMapper;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.CNDServiceSearchCriteria;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of CNDServiceRepository to handle CND application operations.
 */
@Service
@Slf4j
public class CNDServiceRepositoryImpl implements CNDServiceRepository {

    @Autowired
    private CNDConfiguration config;

    @Autowired
    private Producer producer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CNDServiceQueryBuilder queryBuilder;
    
    /**
     * Saves the CND application request data and pushes it to Kafka.
     * 
     * @param cndApplicationRequest The request object containing CND application details.
     */
    @Override
    public void saveCNDApplicationDetail(CNDApplicationRequest cndApplicationRequest) {
        log.info("Saving CND application request for application no: {}",
                cndApplicationRequest.getCndApplication().getApplicationNumber());
        producer.push(config.getCndApplicationSaveTopic(), cndApplicationRequest);
    }

    /**
     * Retrieves CND application details based on the search criteria.
     * 
     * @param cndServiceSearchCriteria The criteria for filtering applications.
     * @return List of CNDApplicationDetail matching the criteria.
     */
    @Override
    public List<CNDApplicationDetail> getCNDApplicationDetail(CNDServiceSearchCriteria cndServiceSearchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getCNDApplicationQuery(cndServiceSearchCriteria, preparedStmtList);
        log.info("Final query for getCndApplicationDetails: {} with params: {}", query, preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new GenericRowMapper<>(CNDApplicationDetail.class));
    }

    /**
     * Retrieves the count of CND applications based on search criteria.
     * 
     * @param criteria The criteria to filter applications.
     * @return The total count of matching CND applications.
     */
    @Override
    public Integer getCNDApplicationsCount(CNDServiceSearchCriteria criteria) {
        List<Object> preparedStatement = new ArrayList<>();
        String query = queryBuilder.getCNDApplicationQuery(criteria, preparedStatement);
        
        if (query == null) {
            return 0;
        }

        log.info("Final query for getCNDApplicationsCount: {} with params: {}", query, preparedStatement);
        return jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
    }

    /**
     * Updates CND application details. (Implementation pending)
     * 
     * @param cndApplicationRequest The request object containing updated CND application details.
     */
    @Override
    public void updateCNDApplicationDetail(CNDApplicationRequest cndApplicationRequest) {
    		log.info("Updating CND  request data for booking no : "
    				+ cndApplicationRequest.getCndApplication().getApplicationNumber());
    		producer.push(config.getCndApplicationUpdateTopic(), cndApplicationRequest);

    	}
}
