package org.egov.bpa.repository;

import java.util.ArrayList;

import java.util.List;

import org.egov.bpa.config.BPAConfiguration;
import org.egov.bpa.producer.PreApprovedProducer;
import org.egov.bpa.repository.querybuilder.PreapprovedPlanQueryBuilder;
import org.egov.bpa.repository.rowmapper.PreapprovedPlanMapper;
import org.egov.bpa.web.model.PreapprovedPlan;
import org.egov.bpa.web.model.PreapprovedPlanRequest;
import org.egov.bpa.web.model.PreapprovedPlanSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PreapprovedPlanRepository {

	@Autowired
	private BPAConfiguration config;

	@Autowired
	private PreApprovedProducer producer;

	@Autowired
	private PreapprovedPlanQueryBuilder queryBuilder;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PreapprovedPlanMapper rowMapper;

	/**
	 * Pushes the request on save topic through kafka
	 *
	 * @param preapprovedPlanRequest The PreapprovedPlanRequest create request
	 */
	public void save(PreapprovedPlanRequest preapprovedPlanRequest) {
		producer.push(config.getSavePreApprovedPlanTopicName(), preapprovedPlanRequest);
	}

	/**
	 * PreapprovedPlan search in database
	 *
	 * @param criteria The PreapprovedPlan Search criteria
	 * @return List of PreapprovedPlan from search
	 */
	public List<PreapprovedPlan> getPreapprovedPlansData(PreapprovedPlanSearchCriteria criteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getPreapprovedPlanSearchQuery(criteria, preparedStmtList);
		List<PreapprovedPlan> preapprovedPlans = jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
		return preapprovedPlans;
	}
	
	/**
	 * pushes the request on update topic through kafka
	 * 
	 * @param preapprovedPlanRequest
	 */
	public void update(PreapprovedPlanRequest preapprovedPlanRequest) {
		producer.push(config.getUpdatePreApprovedPlanTopicName(), preapprovedPlanRequest);
	}

}
