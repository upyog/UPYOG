package org.egov.bpa.calculator.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.bpa.calculator.repository.querybuilder.PreapprovedPlanQueryBuilder;
import org.egov.bpa.calculator.repository.rowmapper.PreapprovedPlanMapper;
import org.egov.bpa.calculator.web.models.PreapprovedPlan;
import org.egov.bpa.calculator.web.models.PreapprovedPlanSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PreapprovedPlanRepository {

	@Autowired
	private PreapprovedPlanQueryBuilder queryBuilder;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PreapprovedPlanMapper rowMapper;

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
	
}
