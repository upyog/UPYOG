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


@Service
@Slf4j
public class CNDServiceRepositoryImpl implements CNDServiceRepository {

	@Autowired
	CNDConfiguration config;
	
	@Autowired
	Producer producer;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	CNDServiceQueryBuilder queryBuilder;
	
    @Override
    public void saveCNDApplicationDetail(CNDApplicationRequest cndApplicationRequest) {
    	log.info("Saving CND application request data for appliaction no : "
				+ cndApplicationRequest.getCndApplication().getApplicationNumber());
		producer.push(config.getCndApplicationSaveTopic(), cndApplicationRequest);

    }

    @Override
    public List<CNDApplicationDetail> getCNDApplicationDetail(CNDServiceSearchCriteria cndServiceSearchCriteria) {
    	List<Object> preparedStmtList = new ArrayList<>();

		
		String query = queryBuilder.getCNDApplicationQuery(cndServiceSearchCriteria, preparedStmtList);
		log.info("Final query for getCndApplicationDetails {} and paramsList {} : " , preparedStmtList, query);
		
		return jdbcTemplate.query(query, preparedStmtList.toArray(), new GenericRowMapper<>(CNDApplicationDetail.class));
	
    }

    @Override
    public Integer getCNDApplicationsCount(CNDServiceSearchCriteria criteria) {
    	List<Object> preparedStatement = new ArrayList<>();
		String query = queryBuilder.getCNDApplicationQuery(criteria, preparedStatement);

		if (query == null)
			return 0;

		log.info("Final query for getCNDApplicationQuery {} and paramsList {} : " , preparedStatement);

		Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
		return count;
    }

	@Override
	public void updateCNDApplicationDetail(CNDApplicationRequest cndApplicationRequest) {
		// TODO Auto-generated method stub
		
	}
    }
   