package org.upyog.cdwm.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.kafka.Producer;
import org.upyog.cdwm.repository.CNDServiceRepository;
import org.upyog.cdwm.repository.querybuilder.CNDServiceQueryBuilder;
import org.upyog.cdwm.repository.rowMapper.CNDApplicationDetailRowmapper;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.CNDServiceSearchCriteria;
import org.upyog.cdwm.web.models.DocumentDetail;
import org.upyog.cdwm.web.models.WasteTypeDetail;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of CNDServiceRepository to handle CND application operations.
 */
@Service
@Slf4j
@Repository
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
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new CNDApplicationDetailRowmapper());
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
     * Updates CND application details.
     * 
     * @param cndApplicationRequest The request object containing updated CND application details.
     */
    @Override
    public void updateCNDApplicationDetail(CNDApplicationRequest cndApplicationRequest) {
    		log.info("Updating CND  request data for booking no : "
    				+ cndApplicationRequest.getCndApplication().getApplicationNumber());
    		producer.push(config.getCndApplicationUpdateTopic(), cndApplicationRequest);

    	}
    /**
     * Saves CND waste type details and document details by publishing them to Kafka.
     *
     * <p>This method checks if both waste type details and document details are empty before proceeding.
     * If there are valid details, it constructs a message containing the application ID, waste details,
     * and document details, then pushes it to a Kafka topic for further processing.</p>
     *
     * @param wasteTypeDetails The list of waste type details associated with the application.
     * @param documentDetails The list of document details associated with the application.
     * @param applicationId The unique identifier of the application.
     */
    
    @Override
    public void saveCNDWasteAndDocumentDetail(List<WasteTypeDetail> wasteTypeDetails, List<DocumentDetail> documentDetails, String applicationId) {
        log.info("Inserting waste type and document details into Kafka");

        if ((wasteTypeDetails == null || wasteTypeDetails.isEmpty()) &&
            (documentDetails == null || documentDetails.isEmpty())) {
            log.warn("No waste or document details provided for insertion.");
            return;
        }

        Map<String, Object> cndApplication = new HashMap<>();
        cndApplication.put("applicationId", applicationId);  
        cndApplication.put("wasteTypeDetails", wasteTypeDetails);
        cndApplication.put("documentDetails", documentDetails);

        Map<String, Object> message = new HashMap<>();
        message.put("cndApplication", cndApplication);
      
        producer.push(config.getSaveWasteDocumentApplicationTopic(), message);

        log.info("Published waste type and document details successfully");
    }

    
}
