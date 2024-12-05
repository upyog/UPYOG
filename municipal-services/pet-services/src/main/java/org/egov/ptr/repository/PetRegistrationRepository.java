package org.egov.ptr.repository;

import lombok.extern.slf4j.Slf4j;

import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.repository.builder.PetApplicationQueryBuilder;
import org.egov.ptr.repository.rowmapper.PetApplicationRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class PetRegistrationRepository {

	@Autowired
	private PetApplicationQueryBuilder queryBuilder;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PetApplicationRowMapper rowMapper;

	public List<PetRegistrationApplication> getApplications(PetApplicationSearchCriteria searchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getPetApplicationSearchQuery(searchCriteria, preparedStmtList);
		log.info("Final query: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	}
	
	public List<Map<String, Object>> getAllCounts() {
		List<Map<String, Object>> statusList = null;
		String query = "SELECT SUM(COUNT(*)) OVER () AS total_applications,EXTRACT(MONTH FROM TO_TIMESTAMP(createdtime / 1000)) AS month,COUNT(*) AS application_count FROM eg_wf_processinstance_v2 WHERE modulename = 'PTR' AND action = 'APPROVE' GROUP BY month ORDER BY month";
		statusList =jdbcTemplate.queryForList(query);
        return statusList;
	}
	
}